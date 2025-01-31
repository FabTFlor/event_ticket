package com.eventtickets.eventtickets.controllers;

import com.eventtickets.eventtickets.model.Event;
import com.eventtickets.eventtickets.model.Venue;
import com.eventtickets.eventtickets.repositories.EventRepository;
import com.eventtickets.eventtickets.repositories.VenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private VenueRepository venueRepository;

    // 📌 Crear un evento
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createEvent(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();

        Long venueId = ((Number) request.get("venueId")).longValue();
        Optional<Venue> venue = venueRepository.findById(venueId);

        if (venue.isEmpty()) {
            response.put("ncode", 0);
            response.put("message", "El recinto no existe.");
            return ResponseEntity.ok(response);
        }

        Event event = new Event();
        event.setName((String) request.get("name"));
        event.setVenue(venue.get());
        event.setDate((request.get("date") != null) ? java.time.LocalDateTime.parse((String) request.get("date")) : null);
        event.setStatus((String) request.get("status"));
        event.setEventInfo((String) request.get("eventInfo"));

        Event savedEvent = eventRepository.save(event);

        response.put("ncode", 1);
        response.put("message", "Evento creado exitosamente.");
        response.put("eventId", savedEvent.getId());
        return ResponseEntity.status(201).body(response);
    }

    // 📌 Obtener todos los eventos
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllEvents() {
        Map<String, Object> response = new HashMap<>();
        List<Event> events = eventRepository.findAll();

        response.put("ncode", 1);
        response.put("events", events);
        return ResponseEntity.ok(response);
    }

    // 📌 Obtener eventos por estado
    @GetMapping("/status/{status}")
    public ResponseEntity<Map<String, Object>> getEventsByStatus(@PathVariable String status) {
        Map<String, Object> response = new HashMap<>();
        List<Event> events = eventRepository.findByStatus(status);

        response.put("ncode", 1);
        response.put("events", events);
        return ResponseEntity.ok(response);
    }
}
