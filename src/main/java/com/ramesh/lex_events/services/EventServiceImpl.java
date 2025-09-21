package com.ramesh.lex_events.services;

import com.ramesh.lex_events.dto.request.EventRequest;
import com.ramesh.lex_events.models.Event;
import com.ramesh.lex_events.models.User;
import com.ramesh.lex_events.repositories.EventRepository;
import com.ramesh.lex_events.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class EventServiceImpl implements EventService{

    private final EventRepository eventRepository;
    private final EmailVerificationService emailVerificationService;
    private final CurrentUserService currentUserService;

    public EventServiceImpl(EventRepository eventRepository, UserRepository userRepository, EmailVerificationService emailVerificationService, CurrentUserService currentUserService) {
        this.eventRepository = eventRepository;
        this.emailVerificationService = emailVerificationService;
        this.currentUserService = currentUserService;
    }


    @Override
    public Event createEvent(Event event) {
        User currentUser = currentUserService.getCurrentUser();

        if(event.getIsFree()){
            event.setEntryFee(null);
        }else {
            if(event.getEntryFee() == null || event.getEntryFee().compareTo(BigDecimal.ZERO) <= 0){
                throw new IllegalStateException("Paid event must have a positive entry fee.");
            }
        }
        event.setCreatedBy(currentUser);
        Event saved = eventRepository.save(event);
        //emailVerificationService.clearVerificationState(currentUser);
        return saved;
    }


    @Override
    public List<Event> getUpcomingEvents() {
        return eventRepository.findByDateAfter(LocalDateTime.now());
    }

    @Override
    public List<Event> searchEvents(String keyword) {
        return eventRepository.searchByTitleOrDescriptionIgnoreCase(keyword.toLowerCase());
    }

    @Override
    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Event not found with id: " + id));
    }

    @Override
    public void deleteEventById(Long id) {
        log.info("inside deleteEventById with id: {}", id);
        Event event = getEventById(id);
        eventRepository.delete(event);
        log.info("Deleted event with id: {}", id);
    }

    @Override
    public Event updateEvent(Long id, EventRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No event with id: " + id));
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setLocation(request.getLocation());
        event.setDate(request.getDate());
        event.setIsFree(request.getIsFree());
        event.setEntryFee(request.getIsFree() ? null :  request.getEntryFee());
        event.setShowContactInfo(request.getShowContactInfo());
        Event savedEvent = eventRepository.save(event);
        return savedEvent;
    }

    @Override
    public boolean isOwner(Long eventId) {
        String currentUsername = currentUserService.getCurrentUser().getUserName();
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        return eventOpt.isPresent() &&
                eventOpt.get().getCreatedBy().getUserName().equals(currentUsername);
    }


}
