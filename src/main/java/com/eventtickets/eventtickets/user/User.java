package com.eventtickets.eventtickets.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import com.eventtickets.eventtickets.model.Role;
import com.eventtickets.eventtickets.security.Token;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(nullable = true)
  private String dni;

  @Column(nullable = true)
  private String phoneNumber;

  @OneToMany(mappedBy = "user")
  private List<Token> tokens;

  @ManyToOne
  @JoinColumn(name = "role_id", nullable = false)
  private Role role;

  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @PrePersist
  public void prePersist() {
      if (this.role == null) {
          this.role = new Role();
          this.role.setId(1L); // Establece el ID del rol por defecto
      }
      this.createdAt = LocalDateTime.now(); // Asigna la fecha actual antes de persistir
  }
}
