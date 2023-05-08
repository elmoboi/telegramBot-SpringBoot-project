package com.example.telegrambot.repository;

import com.example.telegrambot.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@Repository("eventRepository")
public interface EventRepository extends JpaRepository<Event, Integer> {
}
