package com.example.telegrambot.entity;

import com.example.telegrambot.enums.BotState;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "user_table", schema = "public")
public class User {

    @JsonProperty("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JsonProperty("name")
    @Column(name = "user_name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("status")
    @Enumerated(EnumType.STRING)
    private BotState status;

    @JsonProperty("user_id")
    private int user_id;

    @Transient
    private String startWord = "";

    public User() {
    }

    public User(int id, String name, String description, BotState status, int user_id) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return  description;
    }
}
