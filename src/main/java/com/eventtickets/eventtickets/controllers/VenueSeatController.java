package com.eventtickets.eventtickets.controllers;

import com.eventtickets.eventtickets.model.VenueSeat;
import com.eventtickets.eventtickets.model.VenueSection;
import com.eventtickets.eventtickets.repositories.VenueSeatRepository;
import com.eventtickets.eventtickets.repositories.VenueSectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/venue-seats")
public class VenueSeatController {

    @Autowired
    private VenueSeatRepository venueSeatRepository;

    @Autowired
    private VenueSectionRepository venueSectionRepository;

    // 📌 Crear asientos en una sección específica
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createVenueSeats(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();

        Long venueSectionId = Long.valueOf(request.get("venueSectionId").toString());
        int rows = Integer.parseInt(request.get("rows").toString());
        int columns = Integer.parseInt(request.get("columns").toString());
        Map<String, Map<String, List<Integer>>> exceptions = (Map<String, Map<String, List<Integer>>>) request.get("exceptions");

        Optional<VenueSection> venueSection = venueSectionRepository.findById(venueSectionId);
        if (venueSection.isEmpty()) {
            response.put("ncode", 0);
            response.put("message", "Sección del recinto no encontrada.");
            return ResponseEntity.ok(response);
        }

        if (!venueSection.get().isNumbered()) {
            response.put("ncode", 2);
            response.put("message", "No se pueden agregar asientos numerados en una sección sin numeración.");
            return ResponseEntity.ok(response);
        }

        for (int i = 1; i <= rows; i++) {
            String rowLabel = getRowLabel(i);
            for (int j = 1; j <= columns; j++) {
                if (exceptions.containsKey(String.valueOf(i)) && exceptions.get(String.valueOf(i)).get("damagedSeats").contains(j)) {
                    continue; // Saltar asientos dañados
                }
                VenueSeat venueSeat = new VenueSeat();
                venueSeat.setVenueSection(venueSection.get());
                venueSeat.setSeatNumber(rowLabel + j);
                venueSeatRepository.save(venueSeat);
            }
        }

        response.put("ncode", 1);
        response.put("message", "Asientos creados exitosamente.");
        return ResponseEntity.status(201).body(response);
    }

    private String getRowLabel(int rowNumber) {
        StringBuilder label = new StringBuilder();
        rowNumber--;
        while (rowNumber >= 0) {
            label.insert(0, (char) ('A' + (rowNumber % 26)));
            rowNumber = (rowNumber / 26) - 1;
        }
        return label.toString();
    }

    // 📌 Obtener todos los asientos de una sección específica
    @GetMapping("/section/{venueSectionId}")
    public ResponseEntity<Map<String, Object>> getVenueSeats(@PathVariable Long venueSectionId) {
        Map<String, Object> response = new HashMap<>();
        Optional<VenueSection> venueSection = venueSectionRepository.findById(venueSectionId);

        if (venueSection.isEmpty()) {
            response.put("ncode", 0);
            response.put("message", "Sección del recinto no encontrada.");
            return ResponseEntity.ok(response);
        }

        List<VenueSeat> venueSeats = venueSeatRepository.findByVenueSection(venueSection.get());
        response.put("ncode", 1);
        response.put("venueSeats", venueSeats);
        return ResponseEntity.ok(response);
    }
}
