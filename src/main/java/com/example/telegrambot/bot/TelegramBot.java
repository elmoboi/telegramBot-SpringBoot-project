package com.example.telegrambot.bot;

import com.example.telegrambot.bot.menu.BotMenu;
import com.example.telegrambot.entity.ConversationHistory;
import com.example.telegrambot.entity.User;
import com.example.telegrambot.enums.AnswerEnum;
import com.example.telegrambot.enums.BotState;
import com.example.telegrambot.enums.GptState;
import com.example.telegrambot.service.conversation.ConversationHistoryService;
import com.example.telegrambot.service.event.EventService;
import com.example.telegrambot.service.user.UserService;
import com.example.telegrambot.enums.Emojis;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Getter
@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final UserService userService;
    private final ConversationHistoryService conversationHistoryService;
    private final EventService eventService;
    private Message requestMessage = new Message();
    private final SendMessage response = new SendMessage();
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("mm");

    private final String botUsername;
    private final String botToken;

    private final TelegramFacade telegramFacade;
    private final BotMenu botMenu;

    public TelegramBot(
            TelegramBotsApi telegramBotsApi,
            @Value("${telegram-bot.name}") String botUsername,
            @Value("${telegram-bot.token}") String botToken,
            UserService userService, ConversationHistoryService conversationHistoryService, EventService eventService, TelegramFacade telegramFacade, BotMenu botMenu) throws TelegramApiException {
        this.conversationHistoryService = conversationHistoryService;
        this.eventService = eventService;
        this.userService = userService;
        this.botToken = botToken;
        this.botUsername = botUsername;
        this.telegramFacade = telegramFacade;
        this.botMenu = botMenu;
        telegramBotsApi.registerBot(this);
    }

    @Override
    public void onRegister() {
        super.onRegister();
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {

        if(update.hasMessage()) {
            requestMessage = update.getMessage();
            response.setChatId(update.getMessage().getChatId());
        }

        var history = new ConversationHistory();
        history.setUserHistoryConversation("");

        var user = new User(
                0,  requestMessage.getChat().getUserName(),
                requestMessage.getText(),BotState.REGISTERED_USER, GptState.DISABLED,requestMessage.getFrom().getId(), AnswerEnum.NO, history);

        if (requestMessage.getText().equals("/start")) {
            if(!userService.isUserExist(user.getUserId())) {
                userService.incert(user);
                log.info("Register new user: {}, id: {}", user.getName(), user.getUserId());
            }
            defaultMsg(response, "Приветствуем вас на нашем диджитальном дне! \n" + Emojis.ROBOT +
                    " Я робот, буду вашим путеводителем, у меня можно узнать: \n" +
                    "\n <b>- Программу мероприятия. </b>" +
                    "\n <b>- Сгенерировать арт у Midjourney. </b>" +
                    "\n <b>- Пообщаться с ChatGPT. </b>" +
                    "\n <b>- Узнать как зарегистрировать chatGPT и Midjourney. </b>\n\n" +
                    "Мной можно управлять при помощи меню, вызывать его можно ключевыми словами: <i>(Menu/menu/Меню/меню)</i>");
        } else if(requestMessage.getText().equals("/stopGPT")) {
            userService.setGptState(GptState.DISABLED,requestMessage.getFrom().getId());
            defaultMsg(response, Emojis.ROBOT + "ChatGPT был отключен!");
            requestMessage.setText("menu");
            try {
                execute(BotMenu.sendInlineKeyBoardMessage(user.getUserId()));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else if(requestMessage.getText().equals("/refreshGPT")) {
            userService.setGptState(GptState.ACTIVE, requestMessage.getFrom().getId());
            defaultMsg(response, Emojis.ROBOT + "ChatGPT был перегружен, контекст удален, продолжайте Ваше общение!");
            User user1 = userService.findUserByUserId(user.getUserId());
            conversationHistoryService.resetMaxContextQuestions(user1.getId());
            conversationHistoryService.setConversationText("", user1.getId());
        } else if(requestMessage.getText().equals("Menu") || requestMessage.getText().equals("menu")
                || requestMessage.getText().equals("меню") || requestMessage.getText().equals("Меню")) {
            if(update.hasMessage() && update.getMessage().hasText()) {
                try {
                    execute(BotMenu.sendInlineKeyBoardMessage(user.getUserId()));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if(update.hasCallbackQuery()){
                try {
                    execute(telegramFacade.handleUpdate(update));
                } catch (TelegramApiException tae) {
                    tae.printStackTrace();
                }
            }
        } else {
            execute(telegramFacade.handleUpdate(update));
        }
    }

    private void defaultMsg(SendMessage response, String msg) throws TelegramApiException {
        response.setText(msg);
        response.setParseMode(ParseMode.HTML);
        execute(response);
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);
    }
}
