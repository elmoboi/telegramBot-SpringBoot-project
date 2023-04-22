package com.example.telegrambot.bot.handlers;

import com.example.telegrambot.enums.BotState;
import com.example.telegrambot.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
@Slf4j
public class PickFavoriteMidjourneyHandler implements InputMessageHandler {
    //TODO: вставить, если нужна будет выборка одного из 4-наборного арта

    private final UserService userService;

    public PickFavoriteMidjourneyHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public SendMessage handle(Message message) {
        if(userService.getBotState(Math.toIntExact(message.getFrom().getId())).equals(BotState.WAITING_PICK_POSITION_ART)) {
            log.info("handle in pickFavorite");
            userService.setBotState(BotState.PICK_ONE_FAVORITE, Math.toIntExact(message.getFrom().getId()));
        }

        return processUserPickPositionMidjourneyQueryInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.WAITING_PICK_POSITION_ART;
    }

    private SendMessage processUserPickPositionMidjourneyQueryInput(Message message) {
        String userAnswer = message.getText();
        int userId = Math.toIntExact(message.getFrom().getId());
        long chatId = message.getChatId();

        BotState botState = userService.getBotState(userId);

        SendMessage replyToUser = null;

        if(botState.equals(BotState.PICK_ONE_FAVORITE)) {
            replyToUser = new SendMessage(String.valueOf(chatId),"Выбирете один из 4х артов, который вам больше всего понравился!");
            //TODO: отправка на голосование
            userService.setBotState(BotState.PICKED_ONE_FAVORITE,userId);
        }

        return replyToUser;
    }
}
