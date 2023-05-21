package com.example.telegrambot.repository;

import com.example.telegrambot.entity.ConversationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("conversationHistoryRepository")
public interface ConversationHistoryRepository extends JpaRepository<ConversationHistory, Integer> {
    ConversationHistory getConversationHistoriesById(int id);

    @Transactional
    @Modifying
    @Query("update ConversationHistory c set c.userHistoryConversation = ?1 where c.id = ?2")
    void setUserHistoryConversationById(String conversation, Integer id);

    @Transactional
    @Modifying
    @Query("update ConversationHistory c set c.maxContextQuestions = c.maxContextQuestions+1 where c.id = ?1")
    void updateQuestionValue(int id);

    @Transactional
    @Modifying
    @Query("update ConversationHistory c set c.maxContextQuestions = 0 where c.id = ?1")
    void resetQuestionValue(int id);
}
