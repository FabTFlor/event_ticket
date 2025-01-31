package com.eventtickets.eventtickets.repositories;

import com.eventtickets.eventtickets.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name); // Buscar rol por nombre
}
