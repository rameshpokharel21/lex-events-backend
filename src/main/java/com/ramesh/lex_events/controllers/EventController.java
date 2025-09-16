package com.ramesh.lex_events.controllers;

import com.ramesh.lex_events.dto.request.EventRequest;
import com.ramesh.lex_events.dto.response.ErrorResponse;
import com.ramesh.lex_events.dto.response.EventResponse;
import com.ramesh.lex_events.mapper.EventMapper;
import com.ramesh.lex_events.models.Event;
import com.ramesh.lex_events.models.User;
import com.ramesh.lex_events.services.CurrentUserService;
import com.ramesh.lex_events.services.EmailVerificationService;
import com.ramesh.lex_events.services.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/events")
@Log4j2
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final EventMapper eventMapper;
    private final EmailVerificationService emailVerificationService;
    private final CurrentUserService currentUserService;

    @Value("${app.require-email-verification}")
    private boolean requireEmailVerification;


    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping
    public ResponseEntity<?> createEvent(@RequestBody @Valid EventRequest eventRequest){
        User creator = currentUserService.getCurrentUser();
        if(requireEmailVerification){
            boolean validOtp = emailVerificationService.isEmailVerified(creator);
            if(!validOtp){
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(new ErrorResponse("OTP expired or Email not verified."));
                }
        }

        Event event = eventMapper.toEntity(eventRequest);
        log.info("Creatng event: {}", event);
        try{
            Event savedEvent = eventService.createEvent(event);
            return ResponseEntity.ok(eventMapper.toResponse(savedEvent));
        }catch(Exception e){
            log.error("Error creating event", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("event creation failed: " + e.getMessage()));
        }
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
