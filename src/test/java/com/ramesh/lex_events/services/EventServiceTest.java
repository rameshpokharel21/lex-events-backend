package com.ramesh.lex_events.services;

import com.ramesh.lex_events.models.Event;
import com.ramesh.lex_events.models.User;
import com.ramesh.lex_events.repositories.EventRepository;
import com.ramesh.lex_events.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EventServiceImpl eventService;

    private User user;
    private User unverifiedUser;
    @BeforeEach
    void setUp(){
        //MockitoAnnotations.openMocks(this);//redundant with @ExtendedWith(MockitoExtension.class)

        user = new User();
        user.setUserId(20L);
        user.setUserName("testUser");
        user.setIsPhoneVerified(true);

        unverifiedUser = new User();
        unverifiedUser.setUserId(21L);
        unverifiedUser.setUserName("unverified");
        unverifiedUser.setIsPhoneVerified(false);
    }

    @Test
    void createEvent_withVerifyPhone_shouldSaveEvent() {


        Event event = new Event();
        event.setTitle("Test event");
        event.setDescription("Event Description");
        event.setLocation("Test Location.");
        event.setDate(LocalDateTime.now().plusDays(5));
        event.setShowContactInfo(true);
        event.setIsFree(false);
        event.setEntryFee(new BigDecimal("15.00"));

        when(eventRepository.save(any(Event.class))).thenAnswer(i -> i.getArgument(0));

        Event result = eventService.createEvent(event, user);

        assertEquals("Test event", result.getTitle());
        verify(eventRepository).save(event);
    }

    @Test
    void createEventWithUnverifiedUser_throwsException(){

        Event event = new Event();
        event.setTitle("Concert");
        event.setDescription("Live music");
        event.setDate(LocalDateTime.now().plusDays(2));
        event.setIsFree(true);
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> eventService.createEvent(event, unverifiedUser));

        assertEquals("Phone number must be verified to create event.", exception.getMessage());
        verify(eventRepository, never()).save(any(Event.class));
    }

}