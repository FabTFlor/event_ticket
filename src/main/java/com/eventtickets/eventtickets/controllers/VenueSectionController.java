package com.eventtickets.eventtickets.controllers;

import com.eventtickets.eventtickets.model.Venue;
import com.eventtickets.eventtickets.model.VenueSection;
import com.eventtickets.eventtickets.model.SectionType;
import com.eventtickets.eventtickets.repositories.VenueRepository;
import com.eventtickets.eventtickets.repositories.VenueSectionRepository;
import com.eventtickets.eventtickets.repositories.SectionTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/venue-sections")
public class VenueSectionController {

    @Autowired
    private VenueSectionRepository venueSectionRepository;

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private SectionTypeRepository sectionTypeRepository;

    // 📌 Crear una nueva sección en un recinto
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createVenueSection(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();

        Long venueId = Long.valueOf(request.get("venueId").toString());
        Long sectionTypeId = Long.valueOf(request.get("sectionTypeId").toString());
        int totalSeats = Integer.parseInt(request.get("totalSeats").toString());
        boolean isNumbered = Boolean.parseBoolean(request.get("isNumbered").toString());

        Optional<Venue> venue = venueRepository.findById(venueId);
        Optional<SectionType> sectionType = sectionTypeRepository.findById(sectionTypeId);

        if (venue.isEmpty() || sectionType.isEmpty()) {
            response.put("ncode", 0);
            response.put("message", "Recinto o tipo de sección no encontrado.");
            return ResponseEntity.ok(response);
        }

        VenueSection venueSection = new VenueSection();
        venueSection.setVenue(venue.get());
        venueSection.setSectionType(sectionType.get());
        venueSection.setTotalSeats(totalSeats);
        venueSection.setNumbered(isNumbered);

        VenueSection savedSection = venueSectionRepository.save(venueSection);

        response.put("ncode", 1);
        response.put("message", "Sección creada exitosamente.");
        response.put("venueSectionId", savedSection.getId());
        return ResponseEntity.status(201).body(response);
    }

    // 📌 Obtener todas las secciones de un recinto específico
    @GetMapping("/venue/{venueId}")
    public ResponseEntity<Map<String, Object>> getVenueSections(@PathVariable Long venueId) {
        Map<String, Object> response = new HashMap<>();
        Optional<Venue> venue = venueRepository.findById(venueId);

        if (venue.isEmpty()) {
            response.put("ncode", 0);
            response.put("message", "Recinto no encontrado.");
            return ResponseEntity.ok(response);
        }

        List<VenueSection> venueSections = venueSectionRepository.findByVenue(venue.get());
        response.put("ncode", 1);
        response.put("venueSections", venueSections);
        return ResponseEntity.ok(response);
    }
}
