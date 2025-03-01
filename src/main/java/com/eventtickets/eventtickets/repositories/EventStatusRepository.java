package com.eventtickets.eventtickets.repositories;


import com.eventtickets.eventtickets.model.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EventStatusRepository extends JpaRepository<EventStatus, Long> {
    Optional<EventStatus> findByName(String name);
}
