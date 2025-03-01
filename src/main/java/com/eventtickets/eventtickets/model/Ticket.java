package com.eventtickets.eventtickets.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

import com.eventtickets.eventtickets.user.User;

@Entity
@Getter
@Setter
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private String serialCode; // UUID único para cada ticket

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event; 

    @ManyToOne
    @JoinColumn(name = "event_section_id", nullable = false)
    private EventSection eventSection;

    @ManyToOne
    @JoinColumn(name = "seat_id", nullable = true) 
    private EventSeat seat; 

    @ManyToOne
    @JoinColumn(name = "original_owner_id", nullable = false)
    private User originalOwner; 

    @Column(nullable = false)
    private int transferCount = 0;

    @Column(nullable = false)
    private LocalDateTime purchaseDate = LocalDateTime.now();

    @PrePersist
    protected void generateSerialCode() {
        this.serialCode = UUID.randomUUID().toString();
    }
}
