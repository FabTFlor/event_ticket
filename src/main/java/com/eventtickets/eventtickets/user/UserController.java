package com.eventtickets.eventtickets.user;

import com.eventtickets.eventtickets.model.Ticket;
import com.eventtickets.eventtickets.repositories.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userRepository.findAll()
                .stream()
                .map(user -> new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole().getName(), user.getCreatedAt()))
                .toList();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getUserProfile() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String email;
            if (principal instanceof UserDetails) {
                email = ((UserDetails) principal).getUsername();
            } else {
                email = principal.toString();
            }

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            return ResponseEntity.ok(new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole().getName(), user.getCreatedAt()));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/me/tickets")
    public ResponseEntity<?> getUserTickets() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String email;
            if (principal instanceof UserDetails) {
                email = ((UserDetails) principal).getUsername();
            } else {
                email = principal.toString();
            }

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            List<Map<String, Object>> tickets = ticketRepository.findByUser(user).stream().map(ticket -> {
                Map<String, Object> ticketData = new HashMap<>();
                ticketData.put("ticketId", ticket.getId());
                ticketData.put("holder", user.getName());
                ticketData.put("eventId", ticket.getEvent().getId());
                ticketData.put("eventName", ticket.getEvent().getName());
                ticketData.put("eventDate", ticket.getEvent().getDate());
                ticketData.put("sectionId", ticket.getEventSection().getId());
                ticketData.put("sectionPrice", ticket.getEventSection().getPrice());
                ticketData.put("sectionName", ticket.getEventSection().getVenueSection().getSectionType().getName());
                ticketData.put("seat", ticket.getSeat() != null ? ticket.getSeat().getId() : null);
                ticketData.put("purchaseDate", ticket.getPurchaseDate());
                return ticketData;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(Map.of("ncode", 1, "tickets", tickets));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Error procesando la solicitud");
        response.put("message", e.getMessage());
        return ResponseEntity.badRequest().body(response);
    }
}
