package com.ramesh.lex_events.services;

import com.ramesh.lex_events.models.Event;
import com.ramesh.lex_events.models.User;
import com.ramesh.lex_events.repositories.EventRepository;
import com.ramesh.lex_events.repositories.UserRepository;
import com.ramesh.lex_events.utils.SecurityUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Log4j2
@Service
public class EventServiceImpl implements EventService{

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EmailVerificationService emailVerificationService;

    public EventServiceImpl(EventRepository eventRepository, UserRepository userRepository, EmailVerificationService emailVerificationService) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.emailVerificationService = emailVerificationService;
    }


    @Override
    public Event createEvent(Event event) {
        User currentUser = SecurityUtils.getCurrentUser(userRepository);

        if(event.getIsFree()){
            event.setEntryFee(null);
        }else {
            if(event.getEntryFee() == null || event.getEntryFee().compareTo(BigDecimal.ZERO) <= 0){
                throw new IllegalStateException("Paid event must have a positive entry fee.");
            }
        }
        event.setCreatedBy(currentUser);
        Event saved = eventRepository.save(event);
        emailVerificationService.clearVerificationState(currentUser);
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
}
