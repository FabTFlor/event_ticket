package com.eventtickets.eventtickets.repositories;

import com.eventtickets.eventtickets.model.VenueSeat;
import com.eventtickets.eventtickets.model.VenueSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VenueSeatRepository extends JpaRepository<VenueSeat, Long> {
    List<VenueSeat> findByVenueSection(VenueSection venueSection); // Obtener asientos por sección
}
