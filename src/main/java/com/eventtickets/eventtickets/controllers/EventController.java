package com.eventtickets.eventtickets.controllers;

import com.eventtickets.eventtickets.model.Event;
import com.eventtickets.eventtickets.model.EventStatus;
import com.eventtickets.eventtickets.model.Venue;
import com.eventtickets.eventtickets.repositories.EventRepository;
import com.eventtickets.eventtickets.repositories.VenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
        return ResponseEntity.badRequest().body(response);
    }

    // Validar estado del evento
    String statusString = (String) request.get("status");
    EventStatus status;
    try {
        status = EventStatus.valueOf(statusString.toUpperCase());
    } catch (IllegalArgumentException e) {
        response.put("ncode", 0);
        response.put("message", "Estado de evento no válido. Usa: PENDING, ACTIVE, FINISHED, CANCELED.");
        return ResponseEntity.badRequest().body(response);
    }

    // Validar fecha
    String dateString = (String) request.get("date");
    LocalDateTime date;
    try {
        date = LocalDateTime.parse(dateString);
    } catch (Exception e) {
        response.put("ncode", 0);
        response.put("message", "Formato de fecha inválido. Usa formato ISO-8601 (YYYY-MM-DDTHH:MM:SS).");
        return ResponseEntity.badRequest().body(response);
    }

    Event event = new Event();
    event.setName((String) request.get("name"));
    event.setVenue(venue.get());
    event.setDate(date);
    event.setStatus(status);
    event.setEventInfo((String) request.get("eventInfo"));

    // ✅ Agregar el campo `imageUrl` si está presente en la solicitud
    if (request.containsKey("imageUrl")) {
        event.setImageUrl((String) request.get("imageUrl"));
    }

    Event savedEvent = eventRepository.save(event);

    response.put("ncode", 1);
    response.put("message", "Evento creado exitosamente.");
    response.put("eventId", savedEvent.getId());
    response.put("imageUrl", savedEvent.getImageUrl()); // ✅ Incluir la imagen en la respuesta

    return ResponseEntity.status(201).body(response);
}


    // 📌 Obtener un evento por ID
@GetMapping("/{id}")
public ResponseEntity<Map<String, Object>> getEventById(@PathVariable Long id) {
    Map<String, Object> response = new HashMap<>();
    
    Optional<Event> event = eventRepository.findById(id);
    if (event.isEmpty()) {
        response.put("ncode", 0);
        response.put("message", "Evento no encontrado.");
        return ResponseEntity.badRequest().body(response);
    }

    response.put("ncode", 1);
    response.put("event", event.get());
    return ResponseEntity.ok(response);
}


    // 📌 Obtener todos los eventos
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllEvents() {
        Map<String, Object> response = new HashMap<>();
        List<Event> events = eventRepository.findAll();

        List<Map<String, Object>> eventsList = new ArrayList<>();
        for (Event event : events) {
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("eventId", event.getId());
            eventData.put("name", event.getName());
            eventData.put("venue", event.getVenue());
            eventData.put("date", event.getDate());
            eventData.put("status", event.getStatus());
            eventData.put("eventInfo", event.getEventInfo());
            eventData.put("imageUrl", event.getImageUrl());
            eventData.put("totalTicketsSold", event.getTotalTicketsSold());
            eventsList.add(eventData);
        }

        response.put("ncode", 1);
        response.put("events", eventsList);
        return ResponseEntity.ok(response);
    }


    // 📌 Actualizar un evento por ID
@PutMapping("/{id}")
public ResponseEntity<Map<String, Object>> updateEvent(@PathVariable Long id, @RequestBody Map<String, Object> request) {
    Map<String, Object> response = new HashMap<>();

    Optional<Event> existingEvent = eventRepository.findById(id);
    if (existingEvent.isEmpty()) {
        response.put("ncode", 0);
        response.put("message", "Evento no encontrado.");
        return ResponseEntity.badRequest().body(response);
    }

    Event event = existingEvent.get();

    // Actualizar nombre si se proporciona
    if (request.containsKey("name")) {
        event.setName((String) request.get("name"));
    }

    // Actualizar recinto si se proporciona
    if (request.containsKey("venueId")) {
        Long venueId = ((Number) request.get("venueId")).longValue();
        Optional<Venue> venue = venueRepository.findById(venueId);
        if (venue.isPresent()) {
            event.setVenue(venue.get());
        } else {
            response.put("ncode", 0);
            response.put("message", "El recinto especificado no existe.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Actualizar fecha si se proporciona
    if (request.containsKey("date")) {
        try {
            event.setDate(LocalDateTime.parse((String) request.get("date")));
        } catch (Exception e) {
            response.put("ncode", 0);
            response.put("message", "Formato de fecha inválido. Usa formato ISO-8601 (YYYY-MM-DDTHH:MM:SS).");
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Actualizar estado si se proporciona
    if (request.containsKey("status")) {
        try {
            event.setStatus(EventStatus.valueOf(((String) request.get("status")).toUpperCase()));
        } catch (IllegalArgumentException e) {
            response.put("ncode", 0);
            response.put("message", "Estado de evento no válido. Usa: PENDING, ACTIVE, FINISHED, CANCELED.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Actualizar información del evento si se proporciona
    if (request.containsKey("eventInfo")) {
        event.setEventInfo((String) request.get("eventInfo"));
    }

    // Guardar cambios
    eventRepository.save(event);

    response.put("ncode", 1);
    response.put("message", "Evento actualizado exitosamente.");
    response.put("eventId", event.getId());
    return ResponseEntity.ok(response);
}


    // 📌 Obtener eventos por estado
    @GetMapping("/status/{status}")
    public ResponseEntity<Map<String, Object>> getEventsByStatus(@PathVariable String status) {
        Map<String, Object> response = new HashMap<>();

        try {
            EventStatus eventStatus = EventStatus.valueOf(status.toUpperCase());
            List<Event> events = eventRepository.findByStatus(eventStatus);
            response.put("ncode", 1);
            response.put("events", events);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("ncode", 0);
            response.put("message", "Estado no válido. Usa: PENDING, ACTIVE, FINISHED, CANCELED.");
            return ResponseEntity.badRequest().body(response);
        }
    }
}
