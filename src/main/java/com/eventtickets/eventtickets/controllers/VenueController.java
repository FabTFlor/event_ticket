package com.eventtickets.eventtickets.controllers;

import com.eventtickets.eventtickets.model.Venue;
import com.eventtickets.eventtickets.repositories.VenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/venues")
public class VenueController {

    @Autowired
    private VenueRepository venueRepository;

    /**
     * 📌 Obtener todos los recintos
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllVenues() {
        List<Venue> venues = venueRepository.findAll();
        Map<String, Object> response = new HashMap<>();
        response.put("ncode", 1);
        response.put("venues", venues);
        return ResponseEntity.ok(response);
    }

    /**
     * 📌 Obtener un recinto por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getVenueById(@PathVariable Long id) {
        Optional<Venue> venue = venueRepository.findById(id);
        Map<String, Object> response = new HashMap<>();

        if (venue.isEmpty()) {
            response.put("ncode", 0);
            response.put("message", "Recinto no encontrado.");
            return ResponseEntity.ok(response);
        }

        response.put("ncode", 1);
        response.put("venue", venue.get());
        return ResponseEntity.ok(response);
    }

    /**
     * 📌 Registrar un nuevo recinto
     */
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createVenue(@RequestBody Venue venue) {
        Map<String, Object> response = new HashMap<>();

        if (venueRepository.findByName(venue.getName()).isPresent()) {
            response.put("ncode", 2);
            response.put("message", "El recinto ya existe.");
            return ResponseEntity.ok(response);
        }

        Venue savedVenue = venueRepository.save(venue);
        response.put("ncode", 1);
        response.put("message", "Recinto creado con éxito.");
        response.put("venueId", savedVenue.getId());

        return ResponseEntity.status(201).body(response);
    }

    /**
     * 📌 Actualizar un recinto
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateVenue(@PathVariable Long id, @RequestBody Venue venueDetails) {
        Optional<Venue> venue = venueRepository.findById(id);
        Map<String, Object> response = new HashMap<>();

        if (venue.isEmpty()) {
            response.put("ncode", 0);
            response.put("message", "Recinto no encontrado.");
            return ResponseEntity.ok(response);
        }

        Venue existingVenue = venue.get();
        existingVenue.setName(venueDetails.getName());
        existingVenue.setLocation(venueDetails.getLocation());

        venueRepository.save(existingVenue);

        response.put("ncode", 1);
        response.put("message", "Recinto actualizado con éxito.");
        return ResponseEntity.ok(response);
    }

    /**
     * 📌 Eliminar un recinto
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteVenue(@PathVariable Long id) {
        Optional<Venue> venue = venueRepository.findById(id);
        Map<String, Object> response = new HashMap<>();

        if (venue.isEmpty()) {
            response.put("ncode", 0);
            response.put("message", "Recinto no encontrado.");
            return ResponseEntity.ok(response);
        }

        venueRepository.deleteById(id);
        response.put("ncode", 1);
        response.put("message", "Recinto eliminado correctamente.");
        return ResponseEntity.ok(response);
    }
}
