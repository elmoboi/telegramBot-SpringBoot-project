package com.example.telegrambot.entity;

import com.example.telegrambot.enums.AnswerEnum;
import com.example.telegrambot.enums.BotState;
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

    @JsonProperty("name")
    @Column(name = "user_name")
    private String name;

    @JsonProperty("description")
    @Column(name = "description")
    private String description;

    @JsonProperty("status")
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BotState status;

    @JsonProperty("user_id")
    @Column(name = "user_id")
    private long user_id;

    @JsonProperty("contestAnswer")
    @Enumerated(EnumType.STRING)
    @Column(name = "is_already_sent")
    private AnswerEnum isAlreadySent;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", user_id=" + user_id +
                ", isAlreadySent=" + isAlreadySent +
                '}';
    }
}
