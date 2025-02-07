package com.eventtickets.eventtickets.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "event_sections")
public class EventSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne
    @JoinColumn(name = "venue_section_id", nullable = false)
    private VenueSection venueSection;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private boolean isNumbered;

    @Column(nullable = true)
    private Integer remainingTickets;

    @PrePersist
    @PreUpdate
    private void validateRemainingTickets() {
        if (!isNumbered && (remainingTickets == null || remainingTickets < 0)) {
            remainingTickets = 0;
        }
    }
}