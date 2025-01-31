package com.eventtickets.eventtickets.controllers;

import com.eventtickets.eventtickets.model.*;
import com.eventtickets.eventtickets.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventSectionRepository eventSectionRepository;

    @Autowired
    private EventSeatRepository eventSeatRepository;

    // 📌 Comprar un boleto (con o sin asiento numerado)
    @PostMapping("/buy")
    public ResponseEntity<Map<String, Object>> buyTicket(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();

        Long userId = ((Number) request.get("userId")).longValue();
        Long eventSectionId = ((Number) request.get("eventSectionId")).longValue();
        Long seatId = request.containsKey("seatId") ? ((Number) request.get("seatId")).longValue() : null;

        Optional<User> user = userRepository.findById(userId);
        Optional<EventSection> eventSection = eventSectionRepository.findById(eventSectionId);

        if (user.isEmpty() || eventSection.isEmpty()) {
            response.put("ncode", 0);
            response.put("message", "Usuario o sección no encontrados.");
            return ResponseEntity.ok(response);
        }

        if (seatId != null) {
            Optional<EventSeat> seat = eventSeatRepository.findById(seatId);
            if (seat.isEmpty() || !seat.get().isAvailable()) {
                response.put("ncode", 2);
                response.put("message", "Asiento no disponible.");
                return ResponseEntity.ok(response);
            }
            seat.get().setAvailable(false);
            eventSeatRepository.save(seat.get());
        }

        Ticket ticket = new Ticket();
        ticket.setUser(user.get());
        ticket.setEvent(eventSection.get().getEvent());
        ticket.setEventSection(eventSection.get());
        ticket.setSeat(seatId != null ? eventSeatRepository.findById(seatId).get() : null);
        ticket.setOriginalOwner(user.get());
        ticket.setPurchaseDate(LocalDateTime.now());

        ticketRepository.save(ticket);

        response.put("ncode", 1);
        response.put("message", "Compra exitosa.");
        response.put("ticketId", ticket.getId());
        return ResponseEntity.status(201).body(response);
    }

    // 📌 Transferir un boleto
    @PostMapping("/transfer")
    public ResponseEntity<Map<String, Object>> transferTicket(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();

        Long ticketId = ((Number) request.get("ticketId")).longValue();
        Long newUserId = ((Number) request.get("newUserId")).longValue();

        Optional<Ticket> ticket = ticketRepository.findById(ticketId);
        Optional<User> newUser = userRepository.findById(newUserId);

        if (ticket.isEmpty() || newUser.isEmpty()) {
            response.put("ncode", 0);
            response.put("message", "Boleto o usuario no encontrados.");
            return ResponseEntity.ok(response);
        }

        if (ticket.get().getUser().getId().equals(newUserId)) {
            response.put("ncode", 2);
            response.put("message", "No puedes transferirte un boleto a ti mismo.");
            return ResponseEntity.ok(response);
        }

        ticket.get().setUser(newUser.get());
        ticket.get().setTransferCount(ticket.get().getTransferCount() + 1);
        ticketRepository.save(ticket.get());

        response.put("ncode", 1);
        response.put("message", "Boleto transferido exitosamente.");
        return ResponseEntity.ok(response);
    }
}
