package com.eventtickets.eventtickets.repositories;

import com.eventtickets.eventtickets.model.Ticket;
import com.eventtickets.eventtickets.model.EventSection;
import com.eventtickets.eventtickets.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByUser(User user); // Obtener todas las entradas de un usuario
    List<Ticket> findByEventId(Long eventId); // Obtener todas las entradas de un evento

    // Método para contar tickets vendidos en una sección de evento
    int countByEventSection(EventSection eventSection);

    // Método para buscar tickets de un usuario en una sección específica
    List<Ticket> findByEventSectionIdAndUserId(Long eventSectionId, Long userId);
}
