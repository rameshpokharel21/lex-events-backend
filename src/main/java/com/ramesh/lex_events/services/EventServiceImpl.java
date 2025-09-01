package com.ramesh.lex_events.services;

import com.ramesh.lex_events.models.Event;
import com.ramesh.lex_events.models.User;
import com.ramesh.lex_events.repositories.EventRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class EventServiceImpl implements EventService{

    private final EventRepository eventRepository;


    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;

    }


    @Override
    public Event createEvent(Event event, User creator) {
        if(!creator.getIsPhoneVerified()){
            throw new IllegalStateException("Phone number must be verified to create event.");
        }

        if(event.getIsFree()){
            event.setEntryFee(null);
        }else {
            if(event.getEntryFee() == null || event.getEntryFee().compareTo(BigDecimal.ZERO) <= 0){
                throw new IllegalStateException("Paid event must have a positive entry fee.");
            }
        }
        event.setCreatedBy(creator);
        return eventRepository.save(event);
    }


    @Override
    public List<Event> getUpcomingEvents() {
        return eventRepository.findByDateAfter(LocalDateTime.now());
    }

    @Override
    public List<Event> searchEvents(String keyword) {
        return eventRepository.searchByTitleOrDescription(keyword.toLowerCase());
    }

    @Override
    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Event not found with id: " + id));
    }
}
