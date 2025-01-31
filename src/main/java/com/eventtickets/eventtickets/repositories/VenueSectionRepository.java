package com.eventtickets.eventtickets.repositories;

import com.eventtickets.eventtickets.model.VenueSection;
import com.eventtickets.eventtickets.model.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VenueSectionRepository extends JpaRepository<VenueSection, Long> {
    List<VenueSection> findByVenue(Venue venue); // Obtener secciones por recinto
}
