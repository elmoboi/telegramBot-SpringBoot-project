package com.example.telegrambot.bot.handlers;

import com.example.telegrambot.enums.AnswerEnum;
import com.example.telegrambot.enums.BotState;
import com.example.telegrambot.service.user.UserService;
import com.example.telegrambot.enums.Emojis;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class FillingMidjourneyHandler implements InputMessageHandler {

    private final UserService userService;

    public FillingMidjourneyHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public SendMessage handle(Message message) {
        if(userService.getBotState(message.getFrom().getId()).equals(BotState.WAITING_REQUEST_MIDJOURNEY)) {
            userService.setBotState(BotState.ASK_QUERY_MIDJOURNEY, message.getFrom().getId());
        }

        return processUserMidjourneyQueryInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.WAITING_REQUEST_MIDJOURNEY;
    }

    private SendMessage processUserMidjourneyQueryInput(Message message) {
        String userAnswer = message.getText();
        long userId = message.getFrom().getId();
        long chatId = message.getChatId();

        BotState botState = userService.getBotState(userId);

        SendMessage replyToUser = null;

        if(botState.equals(BotState.ASK_QUERY_MIDJOURNEY)) {
            String regex = "[а-яёА-ЯЁ]+";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(userAnswer);
            if(matcher.find() || userAnswer.startsWith("/imagine prompt:") || userAnswer.startsWith("/") || userAnswer.contains("/")) {
                replyToUser = new SendMessage(String.valueOf(chatId), Emojis.ROBOT + "Неверный запрос, проверьте нет ли в вашем запросе кирилицы или: " +
                        "\n<code>\"/imagine prompt:\"</code>, <code>\"/\"</code>, и повторите попытку!");
                replyToUser.setParseMode(ParseMode.HTML);
                userService.setBotState(BotState.WAITING_REQUEST_MIDJOURNEY,userId);
            } else {
                replyToUser = new SendMessage(String.valueOf(chatId),Emojis.ROBOT + "Спасибо, ожидайте Ваш арт, он в самое ближайшее время появится тут!" + Emojis.CLOCK);
                userService.setMessageUser(userAnswer,userId);
                userService.setBotState(BotState.WAITING_ART,userId);
                userService.setSentStatus(AnswerEnum.NO, userId);
            }
        }

        return replyToUser;
    }
}
