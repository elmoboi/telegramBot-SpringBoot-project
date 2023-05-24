package com.example.telegrambot.bot.handlers;

import com.example.telegrambot.bot.ChatGPTClient;
import com.example.telegrambot.entity.User;
import com.example.telegrambot.enums.GptState;
import com.example.telegrambot.service.conversation.ConversationHistoryService;
import com.example.telegrambot.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class FillingGPTHandler implements InputMessageGPTHandler {
    private final UserService userService;
    private final ConversationHistoryService conversationHistoryService;

    public FillingGPTHandler(UserService userService, ConversationHistoryService conversationHistoryService) {
        this.userService = userService;
        this.conversationHistoryService = conversationHistoryService;
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
        User user = userService.findUserByUserId(userId);
        GptState gptState = userService.getGptState(userId);
        SendMessage replyToUser = null;

        userAnswer = userAnswer.replaceAll("[^A-Za-zА-Яа-я0-9-\s]", "");

        if(gptState.equals(GptState.COMMUNICATING_WITH_GPT)) {
            String previousQuestionsAndAnswers = conversationHistoryService.getConversationText(user.getId());
            int curentQuestions = conversationHistoryService.getMaxContextQuestions(user.getId());
            List<String> questionsAndAnswersList = new ArrayList<>();

            if(previousQuestionsAndAnswers != null) {
                String[] splitQuestionsAndAnswers = previousQuestionsAndAnswers.split("Q&A");
                questionsAndAnswersList.addAll(Arrays.asList(splitQuestionsAndAnswers).subList(0, curentQuestions));
            }

            String gptResponse = ChatGPTClient.generateResponse(userAnswer, user, questionsAndAnswersList);
            replyToUser = new SendMessage(String.valueOf(userId),gptResponse);
            gptResponse = gptResponse.replaceAll("[^A-Za-zА-Яа-я0-9-\s]", "");
            String updatedHisoryText = userAnswer + ":::" + gptResponse + " Q&A ";
            String sourceHistoryText = conversationHistoryService.getConversationText(user.getId()) + updatedHisoryText;
            conversationHistoryService.setConversationText(sourceHistoryText, user.getId());
            conversationHistoryService.updateQuestionsContextCount(user.getId());
            userService.setGptState(GptState.ACTIVE, userId);
        }

        return replyToUser;
    }
}
