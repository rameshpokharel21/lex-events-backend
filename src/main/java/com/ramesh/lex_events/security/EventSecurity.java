package com.ramesh.lex_events.security;

import com.ramesh.lex_events.services.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventSecurity {
    private final EventService eventService;

    public boolean isOwner(Long eventId){
        return eventService.isOwner(eventId);
    }
}
