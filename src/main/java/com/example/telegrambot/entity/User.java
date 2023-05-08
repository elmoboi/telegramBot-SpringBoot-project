package com.example.telegrambot.entity;

import com.example.telegrambot.enums.AnswerEnum;
import com.example.telegrambot.enums.BotState;
import com.example.telegrambot.enums.GptState;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "user_table", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @JsonProperty("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "user_name")
    private String name;
    @Column(name = "description")
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(name = "MJstatus")
    private BotState status;
    @Enumerated(EnumType.STRING)
    @Column(name = "GPTstatus")
    private GptState GPTstatus;
    @Column(name = "user_id")
    private long user_id;
    @Enumerated(EnumType.STRING)
    @Column(name = "is_already_sent")
    private AnswerEnum isAlreadySent;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", MJstatus=" + status +
                ", user_id=" + user_id +
                ", isAlreadySent=" + isAlreadySent +
                '}';
    }
}
