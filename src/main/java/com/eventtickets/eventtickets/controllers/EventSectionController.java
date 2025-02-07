package com.eventtickets.eventtickets.controllers;

import com.eventtickets.eventtickets.model.Event;
import com.eventtickets.eventtickets.model.EventSection;
import com.eventtickets.eventtickets.model.Ticket;
import com.eventtickets.eventtickets.model.VenueSection;
import com.eventtickets.eventtickets.model.User;
import com.eventtickets.eventtickets.repositories.EventRepository;
import com.eventtickets.eventtickets.repositories.EventSectionRepository;
import com.eventtickets.eventtickets.repositories.VenueSectionRepository;
import com.eventtickets.eventtickets.repositories.TicketRepository;
import com.eventtickets.eventtickets.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

        // 📌 Crear una nueva sección de evento
        @PostMapping("/create")
        public ResponseEntity<Map<String, Object>> createEventSection(@RequestBody Map<String, Object> request) {
            Map<String, Object> response = new HashMap<>();
    
            try {
                // Obtener el ID del evento y verificar si existe
                Long eventId = ((Number) request.get("eventId")).longValue();
                Optional<Event> event = eventRepository.findById(eventId);
                if (event.isEmpty()) {
                    response.put("ncode", 0);
                    response.put("message", "El evento no existe.");
                    return ResponseEntity.badRequest().body(response);
                }
    
                // Obtener el ID de la sección del recinto y verificar si existe
                Long venueSectionId = ((Number) request.get("venueSectionId")).longValue();
                Optional<VenueSection> venueSection = venueSectionRepository.findById(venueSectionId);
                if (venueSection.isEmpty()) {
                    response.put("ncode", 0);
                    response.put("message", "La sección del recinto no existe.");
                    return ResponseEntity.badRequest().body(response);
                }
    
                // Verificar si la sección es numerada
                boolean isNumbered = Boolean.parseBoolean(request.get("isNumbered").toString());
    
                // Manejo seguro de `price`
                double price;
                try {
                    price = Double.parseDouble(request.get("price").toString());
                } catch (NumberFormatException e) {
                    response.put("ncode", 0);
                    response.put("message", "El precio debe ser un número válido.");
                    return ResponseEntity.badRequest().body(response);
                }
    
                // Manejo seguro de `remainingTickets` si no es numerado
                Integer remainingTickets = null;
                if (!isNumbered) {
                    try {
                        remainingTickets = Integer.parseInt(request.get("remainingTickets").toString());
                        if (remainingTickets < 0) {
                            response.put("ncode", 0);
                            response.put("message", "El número de cupos no puede ser negativo.");
                            return ResponseEntity.badRequest().body(response);
                        }
                    } catch (Exception e) {
                        response.put("ncode", 0);
                        response.put("message", "El número de cupos debe ser un número válido.");
                        return ResponseEntity.badRequest().body(response);
                    }
                }
    
                // Crear la nueva sección del evento
                EventSection eventSection = new EventSection();
                eventSection.setEvent(event.get());
                eventSection.setVenueSection(venueSection.get());
                eventSection.setPrice(price);
                eventSection.setNumbered(isNumbered);
                eventSection.setRemainingTickets(remainingTickets);
    
                EventSection savedEventSection = eventSectionRepository.save(eventSection);
    
                response.put("ncode", 1);
                response.put("message", "Sección de evento creada exitosamente.");
                response.put("eventSectionId", savedEventSection.getId());
                return ResponseEntity.status(201).body(response);
    
            } catch (Exception e) {
                response.put("ncode", 0);
                response.put("message", "Error al procesar la solicitud.");
                response.put("error", e.getMessage());
                return ResponseEntity.internalServerError().body(response);
            }
        }

        // 📌 Consultar cupos disponibles en una sección no numerada
@GetMapping("/cupos/{eventSectionId}")
public ResponseEntity<Map<String, Object>> getCupos(@PathVariable Long eventSectionId) {
    Map<String, Object> response = new HashMap<>();
    
    // Buscar la sección de evento
    EventSection eventSection = eventSectionRepository.findById(eventSectionId)
            .orElseThrow(() -> new IllegalArgumentException("Sección de evento no encontrada."));

    // Verificar si la sección es no numerada
    if (eventSection.isNumbered()) {
        response.put("ncode", 0);
        response.put("message", "Esta sección no maneja cupos, es numerada.");
        return ResponseEntity.badRequest().body(response);
    }

    // Obtener la cantidad de cupos restantes
    response.put("ncode", 1);
    response.put("message", "Consulta exitosa.");
    response.put("eventSectionId", eventSectionId);
    response.put("remainingTickets", eventSection.getRemainingTickets());

    return ResponseEntity.ok(response);
}

    
        // 📌 Cancelar una reserva y liberar cupos
    @PostMapping("/cancel-reserva")
    public ResponseEntity<Map<String, Object>> cancelReserva(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        Long eventSectionId = ((Number) request.get("eventSectionId")).longValue();
        Long userId = ((Number) request.get("userId")).longValue();
        int quantity = ((Number) request.get("quantity")).intValue();

        EventSection eventSection = eventSectionRepository.findById(eventSectionId)
                .orElseThrow(() -> new IllegalArgumentException("Sección de evento no encontrada."));

        List<Ticket> userTickets = ticketRepository.findByEventSectionIdAndUserId(eventSectionId, userId);

        if (userTickets.size() < quantity) {
            response.put("ncode", 0);
            response.put("message", "No tienes suficientes tickets para cancelar.");
            return ResponseEntity.badRequest().body(response);
        }

        // Eliminar los tickets de la base de datos
        for (int i = 0; i < quantity; i++) {
            ticketRepository.delete(userTickets.get(i));
        }

        // Incrementar los cupos disponibles en la sección del evento
        eventSection.setRemainingTickets(eventSection.getRemainingTickets() + quantity);
        eventSectionRepository.save(eventSection);

        response.put("ncode", 1);
        response.put("message", "Reserva cancelada, cupos liberados.");
        response.put("remainingTickets", eventSection.getRemainingTickets());
        return ResponseEntity.ok(response);
    }

        // 📌 Obtener Secciones de un Evento
@GetMapping("/event/{eventId}")
public ResponseEntity<Map<String, Object>> getEventSections(@PathVariable Long eventId) {
    Map<String, Object> response = new HashMap<>();
    List<EventSection> eventSections = eventSectionRepository.findByEventId(eventId);

    if (eventSections.isEmpty()) {
        response.put("ncode", 0);
        response.put("message", "No se encontraron secciones para este evento.");
        return ResponseEntity.ok(response);
    }

    // Convertimos a una estructura más limpia en la respuesta
    List<Map<String, Object>> sectionsList = new ArrayList<>();
    for (EventSection section : eventSections) {
        Map<String, Object> sectionData = new HashMap<>();
        sectionData.put("id", section.getId());
        sectionData.put("eventId", section.getEvent().getId());
        sectionData.put("venueSectionId", section.getVenueSection().getId());
        sectionData.put("price", section.getPrice());
        sectionData.put("isNumbered", section.isNumbered());
        sectionData.put("remainingTickets", section.isNumbered() ? null : section.getRemainingTickets());
        sectionsList.add(sectionData);
    }

    response.put("ncode", 1);
    response.put("eventSections", sectionsList);
    return ResponseEntity.ok(response);
}

    // 📌 Reservar cupos en una sección no numerada (Compra de tickets)
    @PostMapping("/reserve-cupos")
    public ResponseEntity<Map<String, Object>> reserveCupos(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();

        Long eventSectionId = ((Number) request.get("eventSectionId")).longValue();
        Long userId = ((Number) request.get("userId")).longValue();
        int quantity = ((Number) request.get("quantity")).intValue();

        EventSection eventSection = eventSectionRepository.findById(eventSectionId)
                .orElseThrow(() -> new IllegalArgumentException("Sección de evento no encontrada."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        if (eventSection.isNumbered()) {
            response.put("ncode", 0);
            response.put("message", "No puedes comprar cupos en una sección numerada.");
            return ResponseEntity.badRequest().body(response);
        }

        if (eventSection.getRemainingTickets() < quantity) {
            response.put("ncode", 0);
            response.put("message", "No hay suficientes cupos disponibles.");
            return ResponseEntity.badRequest().body(response);
        }

        // Descontar cupos disponibles
        eventSection.setRemainingTickets(eventSection.getRemainingTickets() - quantity);
        eventSectionRepository.save(eventSection);

        // Registrar los tickets en la base de datos
        for (int i = 0; i < quantity; i++) {
            Ticket ticket = new Ticket();
            ticket.setEvent(eventSection.getEvent());
            ticket.setEventSection(eventSection);
            ticket.setUser(user);
            ticket.setOriginalOwner(user);
            ticket.setPurchaseDate(LocalDateTime.now());
            ticketRepository.save(ticket);
        }

        response.put("ncode", 1);
        response.put("message", "Compra realizada con éxito.");
        response.put("remainingTickets", eventSection.getRemainingTickets());
        return ResponseEntity.ok(response);
    }



// 📌 Asignar cupos a una sección no numerada (Incremental)
@PostMapping("/assign-cupos")
public ResponseEntity<Map<String, Object>> assignCupos(@RequestBody Map<String, Object> request) {
    Map<String, Object> response = new HashMap<>();
    Long eventSectionId = ((Number) request.get("eventSectionId")).longValue();
    int newTotalCupos = ((Number) request.get("newTotalCupos")).intValue();

    EventSection eventSection = eventSectionRepository.findById(eventSectionId)
            .orElseThrow(() -> new IllegalArgumentException("Sección de evento no encontrada."));

    if (eventSection.isNumbered()) {
        response.put("ncode", 0);
        response.put("message", "No se pueden asignar cupos a una sección numerada.");
        return ResponseEntity.badRequest().body(response);
    }

    int soldTickets = ticketRepository.countByEventSection(eventSection);

    if (newTotalCupos < soldTickets) {
        response.put("ncode", 0);
        response.put("message", "El nuevo total de cupos no puede ser menor a los cupos vendidos.");
        response.put("soldTickets", soldTickets);
        return ResponseEntity.badRequest().body(response);
    }

    int oldRemainingTickets = eventSection.getRemainingTickets();
    int newAvailableTickets = newTotalCupos - soldTickets;
    eventSection.setRemainingTickets(newAvailableTickets);
    eventSectionRepository.save(eventSection);

    response.put("ncode", 1);
    response.put("message", "Cupos actualizados correctamente.");
    response.put("previousRemainingTickets", oldRemainingTickets);
    response.put("newRemainingTickets", newAvailableTickets);
    response.put("soldTickets", soldTickets);
    return ResponseEntity.ok(response);
}



}

