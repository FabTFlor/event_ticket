package com.eventtickets.eventtickets.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "venue_sections")
public class VenueSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue; // Recinto al que pertenece esta sección

    @ManyToOne
    @JoinColumn(name = "section_type_id", nullable = false)
    private SectionType sectionType; // Tipo de sección (VIP, Platea, Cancha, etc.)

    @Column(nullable = false)
    private int totalSeats; // Número total de asientos en la sección

    @Column(nullable = false)
    private boolean isNumbered; // Indica si los asientos están numerados (true) o no (false)
}
