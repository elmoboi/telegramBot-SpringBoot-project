package com.example.telegrambot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "chat_gpt_conversation", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
public class ConversationHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "user_history_conversation", length = 1000)
    private String userHistoryConversation;
    @OneToOne(mappedBy = "conversationHistory")
    private User user;
    @Column(name = "max_context_questions")
    private int maxContextQuestions;
}
