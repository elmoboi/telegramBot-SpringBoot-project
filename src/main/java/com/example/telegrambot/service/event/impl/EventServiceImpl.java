package com.example.telegrambot.service.event.impl;

import com.example.telegrambot.entity.Event;
import com.example.telegrambot.exceptions.ResourceNotFoundException;
import com.example.telegrambot.repository.EventRepository;
import com.example.telegrambot.service.event.EventService;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.jaxb.core.v2.TODO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EventServiceImpl implements EventService {

    @Autowired
    private EventRepository eventRepository;

    @Override
    public List<Event> getEvents() {
        return eventRepository.findAll();
    }

    @Override
    public Event getNowEvent() {
        List<Event> eventList = getEvents();
        return eventList.stream().filter(event -> event.getTime().isBefore(LocalTime.now())).findFirst().get();
    }

    @Override
    public List<Event> getNextEvents() {
        List<Event> eventList = getEvents();
        return eventList.stream().filter(event -> event.getTime().isAfter(getNowEvent().getTime())).collect(Collectors.toList());
    }
}
