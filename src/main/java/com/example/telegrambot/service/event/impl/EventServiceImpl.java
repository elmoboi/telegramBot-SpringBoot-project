package com.example.telegrambot.service.event.impl;

import com.example.telegrambot.entity.Event;
import com.example.telegrambot.repository.EventRepository;
import com.example.telegrambot.service.event.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
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
        eventList = eventList.stream().filter(event -> event.getTime().isBefore(LocalTime.now())).collect(Collectors.toList());
        return eventList.get(eventList.size()-1);
    }

    @Override
    public List<Event> getNextEvents() {
        List<Event> eventList = getEvents();
        return eventList.stream().filter(event -> event.getTime().isAfter(getNowEvent().getTime())).collect(Collectors.toList());
    }
}
