package com.example.telegrambot.service.conversation;

import com.example.telegrambot.entity.ConversationHistory;

public interface ConversationHistoryService {
    String getConversationText(int id);
    void setConversationText(String conversationText, int id);
    Integer getMaxContextQuestions(int id);
    void setMaxContextQuestions(int id);
    void resetMaxContextQuestions(int id);
}
