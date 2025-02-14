package com.eventtickets.eventtickets.model;

import com.eventtickets.eventtickets.user.User;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "event_seats")
public class EventSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne
    @JoinColumn(name = "venue_seat_id", nullable = false)
    private VenueSeat venueSeat;

    @Column(nullable = false)
    private boolean isAvailable = true;

    @ManyToOne
    @JoinColumn(name = "reserved_by_user_id", nullable = true) // NULL si no está reservado
    private User reservedByUser;

    @ManyToOne
    @JoinColumn(name = "event_section_id", nullable = false)
    private EventSection eventSection;

}
