package com.example.telegrambot.bot;

import com.example.telegrambot.bot.handlers.FillingMidjourneyHandler;
import com.example.telegrambot.entity.Event;
import com.example.telegrambot.enums.BotState;
import com.example.telegrambot.enums.GptState;
import com.example.telegrambot.service.event.EventService;
import com.example.telegrambot.service.user.UserService;
import com.example.telegrambot.utils.Emojis;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@Slf4j
public class TelegramFacade {

    private final UserService userService;
    private final BotStateContext botStateContext;
    FillingMidjourneyHandler fillingMidjourneyHandler;
    private final EventService eventService;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("mm");

    public TelegramFacade(UserService userService, BotStateContext botStateContext, FillingMidjourneyHandler fillingMidjourneyHandler,
                          EventService eventService) {
        this.userService = userService;
        this.botStateContext = botStateContext;
        this.fillingMidjourneyHandler = fillingMidjourneyHandler;
        this.eventService = eventService;
    }

    public BotApiMethod<?> handleUpdate(Update update) {
        log.info("method handleUpdate was started");
        SendMessage replyMessage = null;
        CallbackQuery callbackQuery = update.getCallbackQuery();

        if(update.hasCallbackQuery()) {
                log.info("New callbackQuery from User: {}, userId: {}, with data: {}", update.getCallbackQuery().getFrom().getUserName(),
                        callbackQuery.getFrom().getId(), update.getCallbackQuery().getData());
                return processCallbackQuery(callbackQuery);
        }

        Message message = update.getMessage();
        if(message != null && message.hasText()) {
            log.info("New message from User:{}, chatId: {}, with text: {}, with replyId: {}",
                    message.getFrom().getUserName(), message.getChatId(), message.getText(), message.getMessageId());
            replyMessage = handleInputMessage(message);
        }

        return replyMessage;
    }

    private SendMessage handleInputMessage(Message message) {
        String inputMsg = message.getText();
        long userId = message.getFrom().getId();
        BotState botState;
        SendMessage replyMessage;

        if(userService.getBotState(userId) == null) {
            return new SendMessage(String.valueOf(userId), Emojis.ROBOT +
                    "Похоже меня перезагрузили" + Emojis.FEARFUL + "\nЛибо мы с Вами еще не знакомы" + Emojis.PENSIVE +
                    "\nВ любом случае, активировать меня можно командой" + Emojis.RIGTH_FINGER + "/start");
        }

        switch (inputMsg) {
            default:
                botState = userService.getBotState(userId);
                break;
        }

        userService.setBotState(botState,userId);
        if(!botState.equals(BotState.WAITING_REQUEST_MIDJOURNEY) || botState.equals(GptState.DISABLED)) {
            replyMessage = new SendMessage(String.valueOf(userId),Emojis.ROBOT + " Я не могу общаться без активированного чата с ChatGPT. " +
                    "Активируй чат в разделе меню и тогда мы сможем обсудить интересные вещи" + Emojis.WINK);
        } else {
            replyMessage = botStateContext.processInputMessage(botState,message);
        }


        return replyMessage;
    }

    private BotApiMethod<?> processCallbackQuery(CallbackQuery callbackQuery) {
        log.info("method processCallbackQuery was started");
        final long chatId = callbackQuery.getMessage().getChatId();

        if(callbackQuery.getData().equals("midjourney")) {
            return onMidjourney(callbackQuery);
        } else if(callbackQuery.getData().equals("event")) {
            return onEvent(callbackQuery);
        } else if(callbackQuery.getData().equals("program")) {
            return onProgram(callbackQuery);
        } else if(callbackQuery.getData().equals("next event")) {
            return onNextEvent(callbackQuery);
        } else if(callbackQuery.getData().equals("chatGPT")) {
            return onChatGpt(callbackQuery);
        } else if(callbackQuery.getData().equals("registerBots")) {
            return onRegisterBot(callbackQuery);
        }

        return new SendMessage(String.valueOf(chatId),  Emojis.ROBOT + "Простите, но я не смог распознать ваш запрос, повторите попытку");
    }
    private BotApiMethod<?> onMidjourney(CallbackQuery callbackQuery) {
        final long chatId = callbackQuery.getMessage().getChatId();
        final int userId = Math.toIntExact(callbackQuery.getFrom().getId());
        if(userService.getBotState(userId).equals(BotState.WAITING_ART)) {
            return new SendMessage(String.valueOf(chatId), Emojis.LOCK + " К сожалению, мульти-запрос недоступен, " +
                    "дождитесь пока Midjourney сгенерирует предыдущий запрос перед тем как запришвать новый.");
        }
        String msg1 = """
                    <b> Введите запрос на английском для обработки Midjourney</b>
                    """;
        String msg2 = """
                    <i> Боту можно задать как что-то абстрактное, так и расписать все в мельчайших подробностях.
                    Также Вы можете воспользоваться генератором запросов, в котором есть множество настроек, что придаст конкретики Вашему арту, по этой <a href="https://prompt.noonshot.com/midjourney"> ссылке</a>, но не забудьте убрать:\s
                    "<code>/imagine prompt:</code>"\s
                    перед отправкой, в противном случае Вы не получите сообщение об успешном сохранении запроса</i>""";
        userService.setBotState(BotState.WAITING_REQUEST_MIDJOURNEY,userId);
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), Emojis.KEYBOARD + msg1 + Emojis.UP_FINGER + msg2);
        sendMessage.disableWebPagePreview();
        sendMessage.setParseMode(ParseMode.HTML);
        return sendMessage;
    }

    private BotApiMethod<?> onEvent(CallbackQuery callbackQuery) {
        final long chatId = callbackQuery.getMessage().getChatId();
        StringBuilder stringBuilder = new StringBuilder();
        String response;
        Event nextEvent = eventService.getNextEvents().get(0);
        Event nowEvent = eventService.getNowEvent();
        String title = nowEvent.getTitle();
        String description = nowEvent.getDescription();

        if(title.isEmpty()) {
            title = Emojis.ZZZ + " Запланированных мероприятий сейчас нет.";
            return new SendMessage(String.valueOf(chatId),title);
        }
        response = stringBuilder.append("<b>Название: </b>")
                .append(title)
                .append("\n<b>Описание: </b>")
                .append(description)
                .append("\n<b>Закончится в: </b>")
                .append(nextEvent.getTime().getHour())
                .append(":")
                .append(nextEvent.getTime().format(dtf)).toString();
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), "<b>Сейчас идет: \n\n</b>" + response);
        sendMessage.setParseMode(ParseMode.HTML);
        return sendMessage;
    }

    private BotApiMethod<?> onNextEvent(CallbackQuery callbackQuery) {
        final long chatId = callbackQuery.getMessage().getChatId();
        StringBuilder stringBuilder = new StringBuilder();
        String response = "";
        List<Event> eventList = eventService.getNextEvents();
        if(eventList.isEmpty()) {
            response = Emojis.ZZZ + " Сессий больше не наблюдается.";
            return new SendMessage(String.valueOf(chatId),response);
        }
        for(Event event : eventList) {
            stringBuilder.append("<b>Название: </b>").append(event.getTitle()).append("\n")
                    .append("<b>Описание: </b>").append(event.getDescription()).append("\n")
                    .append("<b>Время начала: </b>").append(event.getTime().getHour()).append(":").append(event.getTime().format(dtf)).append("\n")
                    .append("\n");
            response = stringBuilder.toString();
        }
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), "<b>Следующее по рассписанию: \n\n</b>" + response);
        sendMessage.setParseMode(ParseMode.HTML);
        return sendMessage;
    }

    private BotApiMethod<?> onProgram(CallbackQuery callbackQuery) {
        final long chatId = callbackQuery.getMessage().getChatId();
        StringBuilder stringBuilder = new StringBuilder();
        String response = "";
        List<Event> eventList = eventService.getEvents();
        for(Event event : eventList) {
            stringBuilder.append("<b>Название: </b>").append(event.getTitle()).append("\n")
                    .append("<b>Описание: </b>").append(event.getDescription()).append("\n")
                    .append("<b>Время: </b>").append(event.getTime().getHour()).append(":").append(event.getTime().format(dtf)).append("\n")
                    .append("\n");
            response = stringBuilder.toString();
        }
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), "<b>Программа: \n\n</b>" + response);
        sendMessage.setParseMode(ParseMode.HTML);
        return sendMessage;
    }

    private BotApiMethod<?> onChatGpt(CallbackQuery callbackQuery) {
        final long chatId = callbackQuery.getMessage().getChatId();
        String response = Emojis.ZZZ + "Данный функционал в разработке" + Emojis.ZZZ;
        return new SendMessage(String.valueOf(chatId), response);
    }

    private BotApiMethod<?> onRegisterBot(CallbackQuery callbackQuery) {
        final long chatId = callbackQuery.getMessage().getChatId();
        String mainCautionMsg = """
                <b>Ссылки на сторонние сайты могут быть предоставлены для интереса или удобства пользователей телеграм-бота. Однако мы не несем никакой ответственности за ссылки от нас на другие сайты, и, в частности, мы не несем ответственности за точность или законность их содержания. Мы не несем никакой ответственности за нарушение или упущение политики конфиденциальности третьих лиц</b>""";
        String msg1 = """
                Чтобы зарегистрировать ChatGPT для личного пользования, воспользуйтесь <a href="https://vc.ru/life/584440-kak-zaregistrirovatsya-v-openai-chatgpt-iz-rossii-v-2023-godu">ссылкой</a>.""";
        String tipMsg1 = """
                <i>ChatGPT отлично работает с VPN компании Новартис (кроме почтовых сервисов, придется воспользоваться личным устройством), поэтому от первой ссылки Вам потребуется только сайт sms-activate (поддерживает карты банков РФ), никакого VPN дополнительно скачивать не надо, регистрируетесь по пунктам из ссылки, а моменты с VPN пропускаете, если запустили его на рабочем ноутбуке.</i>""";
        String msg2 = """
                Чтобы пользоваться midjourney придется потрудиться, а именно выполнить пункты с этого <a href="https://vc.ru/u/1075657-denis-zelenykh/576110-kak-oplatit-podpisku-midjourney-iz-rossii">сайта</a>.""";
        String msg3 = """
                Есть и быстрый вариант, но подойдет не всем: если у Вас есть знакомые с зарубежной картой, Вы можете попросить оплатить Вам подписку. Перед этим зарегистрируйтесь в сервисе Discord по <a href="https://discord.com">ссылке</a>. Далее перейдите по <a href="https://midjourney.com">ссылке midjourney</a>, и перейдите в пункт Subscriptions в разделе профиля.""";
        String tipMsg2 = """
                <i>После успешной оплаты подписки, запросы на генерацию надо производить в официальном discord канале midjourney. Гайд как писать запросы midjourney: <a href="https://dtf.ru/s/595875-neyro-iskusstvo/1299811-kak-polzovatsya-midjourney-ili-kak-ne-potratit-vpustuyu-25-krutok-polnyy-gayd">ссылка</a>. Но тогда пользователи канала будут видеть ваши арты, если Вы не хотите, чтобы другие пользователи видели Ваш арт, то можете сделать отдельный собственный канал discord и добавить туда бота midjourney. Гайд на создание собственного канала и приглашение в него бота midjourney: <a href="https://pikabu.ru/story/midjourney_poshagovyiy_gayd_dlya_absolyutnyikh_novichkov_9875060">ссылка</a>.</i>""";
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), Emojis.EXCLAMATION +""+ Emojis.EXCLAMATION +"\n"+ mainCautionMsg +"\n"+ Emojis.EXCLAMATION + Emojis.EXCLAMATION + "\n\n" + msg1 + "\n" + Emojis.UP_FINGER + tipMsg1 + "\n\n" + msg2 + "\n" + msg3 + "\n" + Emojis.UP_FINGER + tipMsg2);
        sendMessage.setParseMode(ParseMode.HTML);
        sendMessage.disableWebPagePreview();
        return sendMessage;
    }
}
