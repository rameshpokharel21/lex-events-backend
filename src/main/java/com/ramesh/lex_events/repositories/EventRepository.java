package com.ramesh.lex_events.repositories;

import com.ramesh.lex_events.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, Long> {


    List<Event> findByDateAfter(LocalDateTime now);

    @Query(
            "SELECT e FROM Event e WHERE LOWER(e.title) LIKE %:keyword% OR LOWER(e.description) LIKE %:keyword%"
    )
    List<Event> searchByTitleOrDescriptionIgnoreCase(@Param("keyword") String keyword);
}
