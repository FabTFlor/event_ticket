package com.eventtickets.eventtickets.repositories;

import com.eventtickets.eventtickets.model.Event;
import com.eventtickets.eventtickets.model.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByStatus(EventStatus status); // Buscar eventos por estado
}
