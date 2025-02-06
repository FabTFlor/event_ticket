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
import java.util.stream.Collectors;

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

        // Validar que el recinto y tipo de sección existan
        Optional<Venue> venue = venueRepository.findById(venueId);
        Optional<SectionType> sectionType = sectionTypeRepository.findById(sectionTypeId);

        if (venue.isEmpty() || sectionType.isEmpty()) {
            response.put("ncode", 0);
            response.put("message", "Recinto o tipo de sección no encontrado.");
            return ResponseEntity.status(404).body(response);
        }

        // Validar que el número de asientos sea positivo
        if (totalSeats <= 0) {
            response.put("ncode", 2);
            response.put("message", "El número de asientos debe ser mayor a cero.");
            return ResponseEntity.badRequest().body(response);
        }

        // Validar que no exista una sección duplicada en el mismo recinto
        List<VenueSection> existingSections = venueSectionRepository.findByVenue(venue.get());
        boolean sectionExists = existingSections.stream()
                .anyMatch(section -> section.getSectionType().getId().equals(sectionTypeId));

        if (sectionExists) {
            response.put("ncode", 3);
            response.put("message", "Ya existe una sección de este tipo en el recinto.");
            return ResponseEntity.badRequest().body(response);
        }

        // Crear la sección
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

    // 📌 Obtener todas las secciones de un recinto específico (Formato mejorado)
    @GetMapping("/venue/{venueId}")
    public ResponseEntity<Map<String, Object>> getVenueSections(@PathVariable Long venueId) {
        Map<String, Object> response = new HashMap<>();
        Optional<Venue> venue = venueRepository.findById(venueId);

        if (venue.isEmpty()) {
            response.put("ncode", 0);
            response.put("message", "Recinto no encontrado.");
            return ResponseEntity.status(404).body(response);
        }

        List<Map<String, Object>> sections = venueSectionRepository.findByVenue(venue.get()).stream().map(section -> {
            Map<String, Object> sectionData = new HashMap<>();
            sectionData.put("id", section.getId());
            sectionData.put("sectionType", section.getSectionType());
            sectionData.put("totalSeats", section.getTotalSeats());
            sectionData.put("isNumbered", section.isNumbered());
            return sectionData;
        }).collect(Collectors.toList());

        response.put("ncode", 1);
        response.put("venue", venue.get());
        response.put("sections", sections);
        return ResponseEntity.ok(response);
    }

    // 📌 Actualizar una sección existente
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateVenueSection(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        Optional<VenueSection> venueSectionOpt = venueSectionRepository.findById(id);

        if (venueSectionOpt.isEmpty()) {
            response.put("ncode", 0);
            response.put("message", "Sección no encontrada.");
            return ResponseEntity.status(404).body(response);
        }

        VenueSection venueSection = venueSectionOpt.get();

        // Actualizar campos si están presentes en la solicitud
        if (request.containsKey("totalSeats")) {
            int totalSeats = Integer.parseInt(request.get("totalSeats").toString());
            if (totalSeats <= 0) {
                response.put("ncode", 2);
                response.put("message", "El número de asientos debe ser mayor a cero.");
                return ResponseEntity.badRequest().body(response);
            }
            venueSection.setTotalSeats(totalSeats);
        }

        if (request.containsKey("isNumbered")) {
            boolean isNumbered = Boolean.parseBoolean(request.get("isNumbered").toString());
            venueSection.setNumbered(isNumbered);
        }

        venueSectionRepository.save(venueSection);

        response.put("ncode", 1);
        response.put("message", "Sección actualizada exitosamente.");
        return ResponseEntity.ok(response);
    }

    // 📌 Eliminar una sección existente
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteVenueSection(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        Optional<VenueSection> venueSectionOpt = venueSectionRepository.findById(id);

        if (venueSectionOpt.isEmpty()) {
            response.put("ncode", 0);
            response.put("message", "Sección no encontrada.");
            return ResponseEntity.status(404).body(response);
        }

        venueSectionRepository.deleteById(id);
        response.put("ncode", 1);
        response.put("message", "Sección eliminada exitosamente.");
        return ResponseEntity.ok(response);
    }
}
    