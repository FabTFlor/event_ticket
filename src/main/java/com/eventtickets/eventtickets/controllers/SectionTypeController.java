package com.eventtickets.eventtickets.controllers;

import com.eventtickets.eventtickets.model.SectionType;
import com.eventtickets.eventtickets.repositories.SectionTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/section-types")
public class SectionTypeController {

    @Autowired
    private SectionTypeRepository sectionTypeRepository;

    // 📌 Crear un nuevo tipo de sección
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createSectionType(@RequestBody SectionType sectionType) {
        Map<String, Object> response = new HashMap<>();

        if (sectionTypeRepository.findByName(sectionType.getName()) != null) {
            response.put("ncode", 2);
            response.put("message", "El tipo de sección ya existe.");
            return ResponseEntity.ok(response);
        }

        SectionType savedType = sectionTypeRepository.save(sectionType);
        response.put("ncode", 1);
        response.put("message", "Tipo de sección creado exitosamente.");
        response.put("sectionTypeId", savedType.getId());
        return ResponseEntity.status(201).body(response);
    }

    // 📌 Obtener todos los tipos de secciones
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllSectionTypes() {
        List<SectionType> sectionTypes = sectionTypeRepository.findAll();
        Map<String, Object> response = new HashMap<>();
        response.put("ncode", 1);
        response.put("sectionTypes", sectionTypes);
        return ResponseEntity.ok(response);
    }
}
