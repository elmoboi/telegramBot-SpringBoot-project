package com.example.telegrambot.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@RequiredArgsConstructor
@Entity
@Table(name = "main_event_schedule", schema = "public")
public class Event {
    @JsonProperty("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JsonProperty("data")
    @Column(name = "data")
    private LocalDate date;

    @JsonProperty("event_title")
    @Column(name = "event_title")
    private String title;

    @JsonProperty("event_description")
    @Column(name = "event_description")
    private String description;

    @JsonProperty("time")
    @Column(name = "time")
    private LocalTime time;
}
