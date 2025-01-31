package com.eventtickets.eventtickets.controllers;

import com.eventtickets.eventtickets.model.Event;
import com.eventtickets.eventtickets.model.EventSection;
import com.eventtickets.eventtickets.model.VenueSection;
import com.eventtickets.eventtickets.repositories.EventRepository;
import com.eventtickets.eventtickets.repositories.EventSectionRepository;
import com.eventtickets.eventtickets.repositories.VenueSectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/event-sections")
public class EventSectionController {

    @Autowired
    private EventSectionRepository eventSectionRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private VenueSectionRepository venueSectionRepository;

    // 📌 Crear una nueva sección de evento
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createEventSection(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();

        Long eventId = ((Number) request.get("eventId")).longValue();
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isEmpty()) {
            response.put("ncode", 0);
            response.put("message", "El evento no existe.");
            return ResponseEntity.ok(response);
        }

        Long venueSectionId = ((Number) request.get("venueSectionId")).longValue();
        Optional<VenueSection> venueSection = venueSectionRepository.findById(venueSectionId);
        if (venueSection.isEmpty()) {
            response.put("ncode", 0);
            response.put("message", "La sección del recinto no existe.");
            return ResponseEntity.ok(response);
        }

        EventSection eventSection = new EventSection();
        eventSection.setEvent(event.get());
        eventSection.setVenueSection(venueSection.get());
        eventSection.setPrice((Double) request.get("price"));
        eventSection.setRemainingTickets((Integer) request.get("remainingTickets"));
        eventSection.setNumbered((Boolean) request.get("isNumbered"));

        EventSection savedEventSection = eventSectionRepository.save(eventSection);

        response.put("ncode", 1);
        response.put("message", "Sección de evento creada exitosamente.");
        response.put("eventSectionId", savedEventSection.getId());
        return ResponseEntity.status(201).body(response);
    }

    // 📌 Obtener todas las secciones de un evento
    @GetMapping("/event/{eventId}")
    public ResponseEntity<Map<String, Object>> getEventSections(@PathVariable Long eventId) {
        Map<String, Object> response = new HashMap<>();
        List<EventSection> sections = eventSectionRepository.findByEventId(eventId);

        response.put("ncode", 1);
        response.put("eventSections", sections);
        return ResponseEntity.ok(response);
    }
}
