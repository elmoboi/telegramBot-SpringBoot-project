package com.example.telegrambot.bot;

import com.example.telegrambot.bot.menu.BotMenu;
import com.example.telegrambot.entity.User;
import com.example.telegrambot.enums.BotState;
//import com.example.telegrambot.service.Producer;
import com.example.telegrambot.service.event.EventService;
import com.example.telegrambot.service.user.UserService;
import com.pengrad.telegrambot.request.GetMe;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
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
    private final EventService eventService;
    private Message requestMessage = new Message();
    private final SendMessage response = new SendMessage();
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("mm");

    private final String botUsername;
    private final String botToken;

    private final TelegramFacade telegramFacade;
    private final BotMenu botMenu;

    private final long botId = 6006881730L;

    public TelegramBot(
            TelegramBotsApi telegramBotsApi,
            @Value("${telegram-bot.name}") String botUsername,
            @Value("${telegram-bot.token}") String botToken,
            UserService userService, EventService eventService, TelegramFacade telegramFacade, BotMenu botMenu) throws TelegramApiException {
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

        if(update.hasMessage() && update.getMessage().getFrom().getIsBot()) {
            log.info("BOT MESSAGE");
        }

        if(update.hasMessage()) {
            requestMessage = update.getMessage();
            response.setChatId(update.getMessage().getChatId());
        }

        var user = new User(
                0, requestMessage.getChat().getUserName(),
                requestMessage.getText(),BotState.REGISTERED_USER,Math.toIntExact(requestMessage.getFrom().getId()));

        if (update.hasMessage() && requestMessage.hasText())
            log.info("Working onUpdateReceived, request text[{}]", update.getMessage().getText());

        if (requestMessage.getText().equals("/start"))
            defaultMsg(response, """
                    Приветствуем вас на нашем диджитальном мероприятии, я буду вашим путеводителем!\s
                    Мною можно управлять при помощи меню, вызвать его можно ключевыми словами: (Menu/menu/Меню/меню)
                    """);


        else if(requestMessage.getText().equals("Menu")
                || requestMessage.getText().equals("menu")
                || requestMessage.getText().equals("меню")
                || requestMessage.getText().equals("Меню")) {
            if(update.hasMessage()) {
                if (update.getMessage().hasText()) {
                    try {
                        execute(BotMenu.sendInlineKeyBoardMessage(update.getMessage().getChatId()));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }else if(update.hasCallbackQuery()){
                try {
                        if(userService.isUserExist(user.getUser_id())) {
                            log.info(user.getName() + " user already inserted");
                            execute(telegramFacade.handleUpdate(update));
                        } else {
                            log.info(user.getName() + " was insert");
                            userService.incert(user);
                            execute(telegramFacade.handleUpdate(update));
                        }
                } catch (TelegramApiException tae) {
                    tae.printStackTrace();
                }
            }
        }

        if (requestMessage.getText().startsWith("/")) {
            user.setStartWord("команда: ");
        } else if (requestMessage.getText().equals("Menu")
                || requestMessage.getText().equals("Меню")
                || requestMessage.getText().equals("menu")
                || requestMessage.getText().equals("меню")) {
            user.setStartWord("меню: ");
        } else {
            execute(telegramFacade.handleUpdate(update));
        }
    }

    private void defaultMsg(SendMessage response, String msg) throws TelegramApiException {
        response.setText(msg);
        execute(response);
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);
    }
}
