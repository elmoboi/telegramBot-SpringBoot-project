package com.example.telegrambot.entity;

import com.example.telegrambot.enums.AnswerEnum;
import com.example.telegrambot.enums.BotState;
import com.example.telegrambot.enums.GptState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.Length;

@Data
@Entity
@Table(name = "user_table", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "user_name")
    private String name;
    @Column(name = "description", length = Length.LOB_DEFAULT)
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(name = "MJstatus")
    private BotState status;
    @Enumerated(EnumType.STRING)
    @Column(name = "GPTstatus")
    private GptState GPTstatus;
    @Column(name = "user_id")
    private long userId;
    @Enumerated(EnumType.STRING)
    @Column(name = "is_already_sent")
    private AnswerEnum isAlreadySent;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id")
    private ConversationHistory conversationHistory;


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", MJstatus=" + status +
                ", Description=" + description +
                ", user_id=" + userId +
                ", isAlreadySent=" + isAlreadySent +
                '}';
    }
}
