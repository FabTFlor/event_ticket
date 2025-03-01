package com.eventtickets.eventtickets.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Getter
@Setter
@Table(name = "venues")
public class Venue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // Nombre del recinto

    @Column(nullable = false)
    private String location; // Dirección o ubicación

    @JsonIgnore
    @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL)
    private List<Event> events; // Relación con eventos que se realizan en este recinto
}
