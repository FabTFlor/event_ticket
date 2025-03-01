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

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = true)
    private EventStatus status;


    @Column(nullable = false, columnDefinition = "TEXT")
    private String eventInfo; // Información general del evento

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<EventSection> eventSections;

    @Column(nullable = true) // Permite valores nulos si no hay imagen
    private String imageUrl; // URL de la imagen del evento

    @Column(nullable = false, updatable = true)
    private LocalDateTime createdAt;



    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int totalTicketsSold;
}
