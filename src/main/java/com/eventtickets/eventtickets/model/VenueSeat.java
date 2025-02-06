package com.eventtickets.eventtickets.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "venue_seats")
public class VenueSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "venue_section_id", nullable = false)
    private VenueSection venueSection; // Sección a la que pertenece el asiento

    @Column(nullable = false)
    private String seatNumber; // Número del asiento (Ejemplo: A1, A2, B1, etc.)
}