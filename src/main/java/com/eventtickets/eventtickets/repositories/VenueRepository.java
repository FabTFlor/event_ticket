package com.eventtickets.eventtickets.repositories;

import com.eventtickets.eventtickets.model.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface VenueRepository extends JpaRepository<Venue, Long> {
    Optional<Venue> findByName(String name); // Buscar recinto por nombre
}
