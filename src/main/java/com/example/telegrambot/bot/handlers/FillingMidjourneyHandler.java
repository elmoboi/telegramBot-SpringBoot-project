package com.example.telegrambot.bot.handlers;

import com.example.telegrambot.enums.BotState;
import com.example.telegrambot.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class FillingMidjourneyHandler implements InputMessageHandler {

    private final UserService userService;

    public FillingMidjourneyHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public SendMessage handle(Message message) {
        if(userService.getBotState(Math.toIntExact(message.getFrom().getId())).equals(BotState.WAITING_REQUEST_MIDJOURNEY)) {
            userService.setBotState(BotState.ASK_QUERY_MIDJOURNEY, Math.toIntExact(message.getFrom().getId()));
        }

        return processUserMidjourneyQueryInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.WAITING_REQUEST_MIDJOURNEY;
    }

    private SendMessage processUserMidjourneyQueryInput(Message message) {
        String userAnswer = message.getText();
        int userId = Math.toIntExact(message.getFrom().getId());
        long chatId = message.getChatId();

        BotState botState = userService.getBotState(userId);

        SendMessage replyToUser = null;

        if(botState.equals(BotState.ASK_QUERY_MIDJOURNEY)) {
            String regex = "[а-яёА-ЯЁ]+";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(userAnswer);
            if(matcher.find()) {
                replyToUser = new SendMessage(String.valueOf(chatId),"Неверный запрос, проверьте нет ли в вашем запросе кирилицы и повторите попытку");
            } else {
                replyToUser = new SendMessage(String.valueOf(chatId),"Спасибо, ожидайте Ваш арт, он в самое ближайшее время появится тут!");
                userService.setMessageUser(userAnswer,userId);
                userService.setBotState(BotState.WAITING_ART,userId);
            }
        }
//        if(botState.equals(BotState.VALIDATE_ART_FROM_MIDJOURNEY)) {
//            replyToUser = new SendMessage(String.valueOf(chatId),"Спасибо, вы уверены что хотите дальше работать с этим артом?");
//        }
//        if(botState.equals(BotState.WAITING_ART)) {
//            replyToUser = new SendMessage(String.valueOf(chatId),"Выберете самую лучшую из 4х артов");
//        }
//        if(botState.equals(BotState.GOT_RESPONSE_FROM_MIDJOURNEY)) {
//            replyToUser = new SendMessage(String.valueOf(chatId),"Вот, что удалось сгенерировать боту Midjourney: \n");
//        }

        return replyToUser;
    }
}
