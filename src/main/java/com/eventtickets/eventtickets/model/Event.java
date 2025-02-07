package com.eventtickets.eventtickets.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @Column(nullable = false)
    private LocalDateTime date;

    @Enumerated(EnumType.STRING) // Usar ENUM en lugar de String
    @Column(nullable = false)
    private EventStatus status;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String eventInfo; // Información general del evento

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<EventSection> eventSections;
}
