package com.example.telegrambot.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.Length;

@Data
@Entity
@Table(name = "chat_gpt_conversation", schema = "public")
@NoArgsConstructor
public class ConversationHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "user_history_conversation", length = Length.LOB_DEFAULT)
    private String userHistoryConversation;
    @Column(name = "max_context_questions")
    private int maxContextQuestions;
}
