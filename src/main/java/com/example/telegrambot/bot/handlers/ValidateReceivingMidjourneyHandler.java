package com.example.telegrambot.bot.handlers;

import com.example.telegrambot.enums.BotState;
import com.example.telegrambot.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
@Slf4j
public class ValidateReceivingMidjourneyHandler implements InputMessageHandler {

    private final UserService userService;

    public ValidateReceivingMidjourneyHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public SendMessage handle(Message message) {
        if(userService.getBotState(Math.toIntExact(message.getFrom().getId())).equals(BotState.GOT_ART_FROM_MIDJOURNEY)) {
            log.info("handle in validating");
            userService.setBotState(BotState.VALIDATE_ART_FROM_MIDJOURNEY, Math.toIntExact(message.getFrom().getId()));
        }

        return processUserValidateMidjourneyQueryInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.WAITING_ART;
    }

    private SendMessage processUserValidateMidjourneyQueryInput(Message message) {
        int userId = Math.toIntExact(message.getFrom().getId());
        long chatId = message.getChatId();

        BotState botState = userService.getBotState(userId);

        SendMessage replyToUser = null;

        if(botState.equals(BotState.VALIDATE_ART_FROM_MIDJOURNEY)) {
            replyToUser = new SendMessage(String.valueOf(chatId), """
                    Спасибо, получение арта подтвержено!\s

                    Если же вас не устроил данный вариант, то вы можете пройти этап: "Меню -> Сгенерировать арт" заново, и получить новый результат.""");
            //TODO: предоставить выборку согласия на конкурс
            userService.setBotState(BotState.VALIDATED_ART_FROM_MIDJOURNEY,userId);
        }

        return replyToUser;
    }
}
