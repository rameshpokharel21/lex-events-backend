package com.ramesh.lex_events.repositories;

import com.ramesh.lex_events.models.Rsvp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RsvpRepository extends JpaRepository<Rsvp, Long> {

    List<Rsvp> findByEvent_EventId(Long eventId);
    List<Rsvp> findByUser_UserId(Long userId);
}
