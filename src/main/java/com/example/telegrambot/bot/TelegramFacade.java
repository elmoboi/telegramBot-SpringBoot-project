package com.example.telegrambot.bot;

import com.example.telegrambot.bot.handlers.FillingMidjourneyHandler;
import com.example.telegrambot.bot.handlers.ValidateReceivingMidjourneyHandler;
import com.example.telegrambot.entity.Event;
import com.example.telegrambot.enums.AnswerEnum;
import com.example.telegrambot.enums.BotState;
import com.example.telegrambot.service.event.EventService;
import com.example.telegrambot.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class TelegramFacade {

    private final UserService userService;
    private final BotStateContext botStateContext;
    FillingMidjourneyHandler fillingMidjourneyHandler;
    ValidateReceivingMidjourneyHandler validateMidjourneyHandler;
    private final EventService eventService;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("mm");

    public TelegramFacade(UserService userService, BotStateContext botStateContext, FillingMidjourneyHandler fillingMidjourneyHandler,
                          EventService eventService, ValidateReceivingMidjourneyHandler validateMidjourneyHandler) {
        this.userService = userService;
        this.botStateContext = botStateContext;
        this.fillingMidjourneyHandler = fillingMidjourneyHandler;
        this.validateMidjourneyHandler = validateMidjourneyHandler;
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

        switch (inputMsg) {
            default:
                botState = userService.getBotState((int) userId);
                break;
        }

        userService.setBotState(botState,(int) userId);
        replyMessage = botStateContext.processInputMessage(botState,message);

        return replyMessage;
    }

    private BotApiMethod<?> processCallbackQuery(CallbackQuery callbackQuery) {
        log.info("method processCallbackQuery was started");
        final long chatId = callbackQuery.getMessage().getChatId();
        final int userId = Math.toIntExact(callbackQuery.getFrom().getId());


        if(callbackQuery.getData().equals("midjourney")) {
            userService.setBotState(BotState.WAITING_REQUEST_MIDJOURNEY,userId);
            return new SendMessage(String.valueOf(chatId), """
                    Введите запрос на английском для обработки Midjourney bot:\s
                    Вы можете 

                    Также Вы можете воспользоваться генератором запросов по ссылке: https://prompt.noonshot.com/midjourney, 
                    но не забудьте убрать "/imagine prompt:" перед отправкой, в противном случае вы не получите сообщение о успешном сохранении запроса""");
        } else if(callbackQuery.getData().equals("contestYes") || callbackQuery.getData().equals("contestNo")) {
            if(callbackQuery.getData().equals("contestYes")) {
                userService.setContestAnswer(AnswerEnum.YES, userId);
                return new SendMessage(String.valueOf(chatId), """
                        Спасибо, надеемся Ваш арт займет первое место!\s

                        Не забудьте отдать свой голос за лучший арт, сделать это можно в Меню, нажав на кнопку "Проголосовать за лучший арт"!""");
            } else {
                userService.setContestAnswer(AnswerEnum.NO, userId);
                return new SendMessage(String.valueOf(chatId),"Спасибо за ответ!");
            }
        } else if(callbackQuery.getData().equals("event")) {
            return onEvent(callbackQuery);
        } else if(callbackQuery.getData().equals("program")) {
            return onProgram(callbackQuery);
        } else if(callbackQuery.getData().equals("next event")) {
            return onNextEvent(callbackQuery);
        } else if(callbackQuery.getData().equals("vote")) {
            return onVote(callbackQuery);
        } else if(callbackQuery.getData().equals("chatGPT")) {
            return onChatGpt(callbackQuery);
        } else if(callbackQuery.getData().equals("registerBots")) {
            return onRegisterBot(callbackQuery);
        }

        return new SendMessage(String.valueOf(chatId), "Простите, но я не смог распознать ваш запрос, повторите попытку");
    }

    private BotApiMethod<?> onEvent(CallbackQuery callbackQuery) {
        final long chatId = callbackQuery.getMessage().getChatId();
        String response;
        String title = eventService.getNowEvent().getTitle();
        String description = eventService.getNowEvent().getDescription();
        if(title.isEmpty()) {
            title = "Запланированных мероприятий сейчас нет";
        }
        response = "Сейчас идет: " + title + "\n" + description;
        return new SendMessage(String.valueOf(chatId), response);
    }

    private BotApiMethod<?> onNextEvent(CallbackQuery callbackQuery) {
        final long chatId = callbackQuery.getMessage().getChatId();
        String response = "";
        StringBuilder program = new StringBuilder();
        List<Event> reversedList = eventService.getNextEvents();
        Collections.reverse(reversedList);
        if(reversedList.isEmpty()) {
            response = "Сессий больше не наблюдается!";
        }
        for(Event event : reversedList) {
            program.append("Название: ").append(event.getTitle()).append("\n")
                    .append("Описание: ").append(event.getDescription()).append("\n")
                    .append("Время: ").append(event.getTime().getHour()).append(":").append(event.getTime().format(dtf)).append("\n")
                    .append("\n");
            response = program.toString();
        }
        return new SendMessage(String.valueOf(chatId), response);
    }

    private BotApiMethod<?> onProgram(CallbackQuery callbackQuery) {
        final long chatId = callbackQuery.getMessage().getChatId();
        String response = "";
        StringBuilder program = new StringBuilder();
        List<Event> reversedList = eventService.getEvents();
        Collections.reverse(reversedList);
        for(Event event : reversedList) {
            program.append("Название: ").append(event.getTitle()).append("\n")
                    .append("Описание: ").append(event.getDescription()).append("\n")
                    .append("Время: ").append(event.getTime().getHour()).append(":").append(event.getTime().format(dtf)).append("\n")
                    .append("\n");
            response = program.toString();
        }
        return new SendMessage(String.valueOf(chatId), response);
    }

    private BotApiMethod<?> onVote(CallbackQuery callbackQuery) {
        final long chatId = callbackQuery.getMessage().getChatId();
        String response = "Данный функционал еще не разработан \n";
        return new SendMessage(String.valueOf(chatId), response);
    }

    private BotApiMethod<?> onChatGpt(CallbackQuery callbackQuery) {
        final long chatId = callbackQuery.getMessage().getChatId();
        String response = "Данный функционал еще не разработан \n";
        return new SendMessage(String.valueOf(chatId), response);
    }

    private BotApiMethod<?> onRegisterBot(CallbackQuery callbackQuery) {
        final long chatId = callbackQuery.getMessage().getChatId();
        String response = "Данный функционал еще не разработан \n";
        return new SendMessage(String.valueOf(chatId), response);
    }
}
