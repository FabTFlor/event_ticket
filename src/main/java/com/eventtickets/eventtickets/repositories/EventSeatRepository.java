package com.eventtickets.eventtickets.repositories;

import com.eventtickets.eventtickets.model.EventSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EventSeatRepository extends JpaRepository<EventSeat, Long> {
    List<EventSeat> findByEventId(Long eventId); // Obtener todos los asientos de un evento
    List<EventSeat> findByEventIdAndIsAvailableTrue(Long eventId); // Obtener solo asientos disponibles
    List<EventSeat> findByVenueSeatId(Long venueSeatId); // Buscar asientos de un recinto específico
}
