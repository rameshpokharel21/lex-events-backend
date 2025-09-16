package com.ramesh.lex_events.services;

import com.ramesh.lex_events.models.Event;
import com.ramesh.lex_events.models.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


public interface EventService {


    Event createEvent(Event event);


    List<Event> getUpcomingEvents();

    List<Event> searchEvents(String keyword);

    Event getEventById(Long id);

    void deleteEventById(Long id);
}
