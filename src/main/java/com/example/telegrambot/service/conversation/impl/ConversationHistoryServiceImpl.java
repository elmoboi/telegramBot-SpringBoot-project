package com.example.telegrambot.service.conversation.impl;

import com.example.telegrambot.entity.ConversationHistory;
import com.example.telegrambot.repository.ConversationHistoryRepository;
import com.example.telegrambot.service.conversation.ConversationHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConversationHistoryServiceImpl implements ConversationHistoryService {

    private final ConversationHistoryRepository conversationHistoryRepository;

    @Override
    public String getConversationText(int id) {
        ConversationHistory conversationHistory = conversationHistoryRepository.getConversationHistoriesById(id);
        return conversationHistory.getUserHistoryConversation();
    }
    @Override
    public void setConversationText(String conversationText, int id) {
        conversationHistoryRepository.setUserHistoryConversationById(conversationText, id);
    }
    @Override
    public Integer getMaxContextQuestions(int id) {
        return conversationHistoryRepository.getConversationHistoriesById(id).getMaxContextQuestions();
    }

    @Override
    public void updateQuestionsContextCount(int id) {
        conversationHistoryRepository.updateQuestionValue(id);
    }

    @Override
    public void resetMaxContextQuestions(int id) {
        conversationHistoryRepository.resetQuestionValue(id);
    }
}
