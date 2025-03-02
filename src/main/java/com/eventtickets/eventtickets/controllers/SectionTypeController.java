package com.eventtickets.eventtickets.controllers;

import com.eventtickets.eventtickets.model.SectionType;
import com.eventtickets.eventtickets.repositories.SectionTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/section-types")
public class SectionTypeController {

    @Autowired
    private SectionTypeRepository sectionTypeRepository;

    /**
     * 📍 Crear un nuevo tipo de sección
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createSectionType(@RequestBody SectionType sectionType) {
        Map<String, Object> response = new HashMap<>();

        // 🔄 Validación para evitar duplicados por nombre (ignorando mayúsculas/minúsculas)
        if (sectionTypeRepository.findByName(sectionType.getName().toUpperCase()) != null) {
            response.put("ncode", 2);
            response.put("message", "El tipo de sección ya existe.");
            return ResponseEntity.badRequest().body(response);
        }


        // Guardar el tipo de sección
        sectionType.setName(sectionType.getName().toUpperCase()); // Normalizar nombres
        SectionType savedType = sectionTypeRepository.save(sectionType);

        response.put("ncode", 1);
        response.put("message", "Tipo de sección creado exitosamente.");
        response.put("sectionTypeId", savedType.getId());
        return ResponseEntity.status(201).body(response);
    }

    /**
     * 📍 Obtener todos los tipos de secciones
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllSectionTypes() {
        List<SectionType> sectionTypes = sectionTypeRepository.findAll();
        Map<String, Object> response = new HashMap<>();
        response.put("ncode", 1);
        response.put("sectionTypes", sectionTypes);
        return ResponseEntity.ok(response);
    }

    /**
     * 📍 Obtener un tipo de sección por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getSectionTypeById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        Optional<SectionType> sectionTypeOpt = sectionTypeRepository.findById(id);
        if (sectionTypeOpt.isEmpty()) {
            response.put("ncode", 0);
            response.put("message", "Tipo de sección no encontrado.");
            return ResponseEntity.badRequest().body(response);
        }

        response.put("ncode", 1);
        response.put("sectionType", sectionTypeOpt.get());
        return ResponseEntity.ok(response);
    }

    /**
     * 📍 Actualizar un tipo de sección
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateSectionType(@PathVariable Long id, @RequestBody SectionType updatedType) {
        Map<String, Object> response = new HashMap<>();

        Optional<SectionType> existingTypeOpt = sectionTypeRepository.findById(id);
        if (existingTypeOpt.isEmpty()) {
            response.put("ncode", 0);
            response.put("message", "Tipo de sección no encontrado.");
            return ResponseEntity.badRequest().body(response);
        }

        SectionType existingType = existingTypeOpt.get();

        // 🔄 Verificar si el nombre nuevo ya existe en otro registro
        if (!existingType.getName().equals(updatedType.getName().toUpperCase()) &&
            sectionTypeRepository.findByName(updatedType.getName().toUpperCase()) != null) {
            response.put("ncode", 2);
            response.put("message", "Ya existe otro tipo de sección con ese nombre.");
            return ResponseEntity.badRequest().body(response);
        }



        existingType.setName(updatedType.getName().toUpperCase());

        sectionTypeRepository.save(existingType);
        response.put("ncode", 1);
        response.put("message", "Tipo de sección actualizado con éxito.");
        return ResponseEntity.ok(response);
    }

    /**
     * 📍 Eliminar un tipo de sección
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteSectionType(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        Optional<SectionType> sectionTypeOpt = sectionTypeRepository.findById(id);
        if (sectionTypeOpt.isEmpty()) {
            response.put("ncode", 0);
            response.put("message", "Tipo de sección no encontrado.");
            return ResponseEntity.badRequest().body(response);
        }

        sectionTypeRepository.deleteById(id);
        response.put("ncode", 1);
        response.put("message", "Tipo de sección eliminado exitosamente.");
        return ResponseEntity.ok(response);
    }
}
