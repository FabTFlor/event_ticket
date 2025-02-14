package com.eventtickets.eventtickets.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

import com.eventtickets.eventtickets.user.User;

@Entity
@Getter
@Setter
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // Ejemplo: "User", "Admin"

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> users; // Lista de usuarios que tienen este rol
}
