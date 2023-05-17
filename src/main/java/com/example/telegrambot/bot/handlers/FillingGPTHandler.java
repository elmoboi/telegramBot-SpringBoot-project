package com.example.telegrambot.bot.handlers;

import com.example.telegrambot.bot.ChatGPTClient;
import com.example.telegrambot.enums.GptState;
import com.example.telegrambot.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
@Slf4j
public class FillingGPTHandler implements InputMessageGPTHandler {
    private final UserService userService;

    public FillingGPTHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public SendMessage handle(Message message) throws Exception {
        if(userService.getGptState(message.getFrom().getId()).equals(GptState.ACTIVE)) {
            userService.setGptState(GptState.COMMUNICATING_WITH_GPT, message.getFrom().getId());
        }

        return processUserMidjourneyQueryInput(message);
    }

    @Override
    public GptState getHandlerName() {
        return GptState.ACTIVE;
    }

    private SendMessage processUserMidjourneyQueryInput(Message message) throws Exception {
        String userAnswer = message.getText();
        long userId = message.getFrom().getId();

        GptState gptState = userService.getGptState(userId);

        SendMessage replyToUser = null;

        if(gptState.equals(GptState.COMMUNICATING_WITH_GPT)) {
            //ChatGPTClient.checkResponse();
            String gptResponse = ChatGPTClient.generateResponse(userAnswer);
            log.info("Ответ от chatGPT: [{}]", gptResponse);
            replyToUser = new SendMessage(String.valueOf(userId),gptResponse);
            userService.setGptState(GptState.ACTIVE, userId);
        }

        return replyToUser;
    }
}
