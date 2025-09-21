package com.ramesh.lex_events.services;

import com.ramesh.lex_events.dto.request.EventRequest;
import com.ramesh.lex_events.models.Event;

import java.util.List;

public interface EventService {


    Event createEvent(Event event);


    List<Event> getUpcomingEvents();

    List<Event> searchEvents(String keyword);

    Event getEventById(Long id);

    void deleteEventById(Long id);

    Event updateEvent(Long id, EventRequest request);
    boolean isOwner(Long eventId);
}
