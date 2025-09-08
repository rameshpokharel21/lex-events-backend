package com.ramesh.lex_events.controllers;

import com.ramesh.lex_events.dto.request.EventRequest;
import com.ramesh.lex_events.dto.response.EventResponse;
import com.ramesh.lex_events.dto.response.MessageResponse;
import com.ramesh.lex_events.mapper.EventMapper;
import com.ramesh.lex_events.models.Event;
import com.ramesh.lex_events.models.User;
import com.ramesh.lex_events.repositories.EventRepository;
import com.ramesh.lex_events.services.EventService;
import com.ramesh.lex_events.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/events")

public class EventController {
    private final EventService eventService;
    private final EventMapper eventMapper;
    private final UserService userService;

    public EventController(EventService eventService, EventMapper eventMapper, UserService userService) {
        this.eventService = eventService;
        this.eventMapper = eventMapper;
        this.userService = userService;
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping
    public ResponseEntity<?> createEvent(@RequestBody @Valid EventRequest eventRequest,
                                         Authentication authentication){
        String username = authentication.getName();
        User creator = userService.getUserByUsername(username);
        if(creator.getIsEmailVerified() == null || !creator.getIsEmailVerified()){
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("Email not verified, Cannot create event."));
        }
        Event event = eventMapper.toEntity(eventRequest);
        Event savedEvent = eventService.createEvent(event, creator);
        return ResponseEntity.ok(eventMapper.toResponse(savedEvent));
    }


    @GetMapping("/upcoming")
    public List<EventResponse> getUpcomingEvents(){
        return eventService.getUpcomingEvents()
                .stream()
                .map(eventMapper::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<EventResponse> searchEvents(@RequestParam("keyword") String keyword){
        return eventService.searchEvents(keyword)
                .stream()
                .map(eventMapper::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEventDetails(@PathVariable Long id){
        Event event = eventService.getEventById(id);
        EventResponse response = eventMapper.toResponse(event);
        return ResponseEntity.ok(response);
    }
}
