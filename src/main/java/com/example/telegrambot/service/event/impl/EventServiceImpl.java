package com.example.telegrambot.service.event.impl;

import com.example.telegrambot.entity.Event;
import com.example.telegrambot.repository.EventRepository;
import com.example.telegrambot.service.event.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;

    @Override
    public List<Event> getEvents() {
        return eventRepository.findAll();
    }
}
