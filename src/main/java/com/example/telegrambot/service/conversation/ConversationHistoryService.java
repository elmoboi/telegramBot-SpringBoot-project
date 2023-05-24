package com.example.telegrambot.service.conversation;


public interface ConversationHistoryService {
    String getConversationText(int id);
    void setConversationText(String conversationText, int id);
    Integer getMaxContextQuestions(int id);
    void updateQuestionsContextCount(int id);
    void resetMaxContextQuestions(int id);
}
