package com.example.telegrambot.service.event;

import com.example.telegrambot.entity.Event;

import java.util.List;

public interface EventService {
    List<Event> getEvents();
}
