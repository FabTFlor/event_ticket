package com.eventtickets.eventtickets.controllers;

import com.eventtickets.eventtickets.model.*;
import com.eventtickets.eventtickets.repositories.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/event-seats")
public class EventSeatController {

    @Autowired
    private EventSeatRepository eventSeatRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private VenueSeatRepository venueSeatRepository;

    @Autowired
    private UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper(); // Conversor JSON seguro

    // 📌 Crear los asientos de un evento
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createEventSeats(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();

        Long eventId = ((Number) request.get("eventId")).longValue();
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isEmpty()) {
            response.put("ncode", 0);
            response.put("message", "El evento no existe.");
            return ResponseEntity.ok(response);
        }

        try {
            // 🔥 Conversión segura usando Jackson
            List<Integer> venueSeatIds = objectMapper.convertValue(request.get("venueSeatIds"), new TypeReference<>() {});

            List<EventSeat> eventSeats = new ArrayList<>();
            for (Integer venueSeatId : venueSeatIds) {
                Optional<VenueSeat> venueSeat = venueSeatRepository.findById(Long.valueOf(venueSeatId));
                if (venueSeat.isEmpty()) continue;

                EventSeat eventSeat = new EventSeat();
                eventSeat.setEvent(event.get());
                eventSeat.setVenueSeat(venueSeat.get());
                eventSeat.setAvailable(true);
                eventSeats.add(eventSeat);
            }

            eventSeatRepository.saveAll(eventSeats);
            response.put("ncode", 1);
            response.put("message", "Asientos creados para el evento.");
            response.put("createdSeats", eventSeats.size());
            return ResponseEntity.status(201).body(response);

        } catch (Exception e) {
            response.put("ncode", 0);
            response.put("message", "Error al procesar la lista de asientos.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 📌 Obtener asientos disponibles de un evento
    @GetMapping("/available/{eventId}")
    public ResponseEntity<Map<String, Object>> getAvailableSeats(@PathVariable Long eventId) {
        Map<String, Object> response = new HashMap<>();
        List<EventSeat> availableSeats = eventSeatRepository.findByEventIdAndIsAvailableTrue(eventId);

        response.put("ncode", 1);
        response.put("availableSeats", availableSeats);
        return ResponseEntity.ok(response);
    }

    // 📌 Reservar un asiento
    @PostMapping("/reserve")
    public ResponseEntity<Map<String, Object>> reserveSeat(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();

        Long seatId = ((Number) request.get("seatId")).longValue();
        Long userId = ((Number) request.get("userId")).longValue();

        Optional<EventSeat> eventSeat = eventSeatRepository.findById(seatId);
        Optional<User> user = userRepository.findById(userId);

        if (eventSeat.isEmpty() || user.isEmpty()) {
            response.put("ncode", 0);
            response.put("message", "Asiento o usuario no encontrado.");
            return ResponseEntity.ok(response);
        }

        if (!eventSeat.get().isAvailable()) {
            response.put("ncode", 2);
            response.put("message", "Asiento ya reservado.");
            return ResponseEntity.ok(response);
        }

        eventSeat.get().setAvailable(false);
        eventSeat.get().setReservedByUser(user.get());
        eventSeatRepository.save(eventSeat.get());

        response.put("ncode", 1);
        response.put("message", "Asiento reservado exitosamente.");
        return ResponseEntity.ok(response);
    }
}
