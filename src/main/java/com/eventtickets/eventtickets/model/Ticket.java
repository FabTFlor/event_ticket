package com.eventtickets.eventtickets.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

import com.eventtickets.eventtickets.user.User;

@Entity
@Getter
@Setter
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // Usuario que compró el ticket

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event; // Evento al que pertenece el ticket

    @ManyToOne
    @JoinColumn(name = "event_section_id", nullable = false)
    private EventSection eventSection; // Sección del evento

    @ManyToOne
    @JoinColumn(name = "seat_id", nullable = true) 
    private EventSeat seat; // Puede ser NULL si la sección no tiene asientos numerados

    @ManyToOne
    @JoinColumn(name = "original_owner_id", nullable = false)
    private User originalOwner; // Usuario original (para trazabilidad en transferencias)

    @Column(nullable = false)
    private int transferCount = 0; // Cuántas veces ha sido transferido

    @Column(nullable = false)
    private LocalDateTime purchaseDate = LocalDateTime.now();

}
