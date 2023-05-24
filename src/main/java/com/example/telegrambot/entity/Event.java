package com.example.telegrambot.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "main_event_schedule", schema = "public")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "data")
    private LocalDate date;
    @Column(name = "event_title")
    private String title;
    @Column(name = "event_description")
    private String description;
    @Column(name = "time")
    private LocalTime time;
}
