package com.eventtickets.eventtickets.repositories;

import com.eventtickets.eventtickets.model.Ticket;
import com.eventtickets.eventtickets.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByUser(User user); // Obtener todas las entradas de un usuario
    List<Ticket> findByEventId(Long eventId); // Obtener todas las entradas de un evento
}
