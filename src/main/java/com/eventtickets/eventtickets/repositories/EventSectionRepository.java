package com.eventtickets.eventtickets.repositories;

import com.eventtickets.eventtickets.model.EventSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventSectionRepository extends JpaRepository<EventSection, Long> {
    List<EventSection> findByEventId(Long eventId);
    boolean existsByVenueSectionIdAndEventDate(Long venueSectionId, LocalDateTime eventDate);
}