package com.eventtickets.eventtickets.user;

import com.eventtickets.eventtickets.repositories.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userRepository.findAll().stream()
            .map(user -> new UserResponse(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getRole().getName(),
                    user.getDni(),
                    user.getPhoneNumber(),
                    user.getCreatedAt()
            ))
            .toList();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getUserProfile() {
        try {
            String email = getCurrentUserEmail();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            return ResponseEntity.ok(new UserResponse(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getRole().getName(),
                    user.getDni(),
                    user.getPhoneNumber(),
                    user.getCreatedAt()
            ));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/me/tickets")
    public ResponseEntity<?> getUserTickets() {
        try {
            String email = getCurrentUserEmail();
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
                ticketData.put("serialNumber", ticket.getSerialCode());
                return ticketData;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(Map.of("ncode", 1, "tickets", tickets));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    /*
      ------------------------------------------------------------
      NUEVO ENDPOINT PARA ACTUALIZAR USUARIO (validar con contraseña)
      ------------------------------------------------------------
    */
    @PutMapping("/me")
    public ResponseEntity<?> updateUser(@RequestBody UpdateUserRequest request) {
        try {
            String email = getCurrentUserEmail();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            // Validar contraseña actual
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                return ResponseEntity.badRequest().body(Map.of(
                    "ncode", 0,
                    "message", "La contraseña proporcionada es incorrecta."
                ));
            }

            // Actualizar datos opcionales si vienen en la solicitud
            if (request.getName() != null && !request.getName().isBlank()) {
                user.setName(request.getName());
            }
            if (request.getEmail() != null && !request.getEmail().isBlank()) {
                user.setEmail(request.getEmail());
            }
            if (request.getDni() != null && !request.getDni().isBlank()) {
                user.setDni(request.getDni());
            }
            if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) {
                user.setPhoneNumber(request.getPhoneNumber());
            }

            userRepository.save(user);

            return ResponseEntity.ok(Map.of(
                "ncode", 1,
                "message", "Perfil actualizado correctamente.",
                "user", new UserResponse(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getRole().getName(),
                    user.getDni(),
                    user.getPhoneNumber(),
                    user.getCreatedAt()
                )
            ));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private String getCurrentUserEmail() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email;
        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }
        return email;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Error procesando la solicitud");
        response.put("message", e.getMessage());
        return ResponseEntity.badRequest().body(response);
    }


    @PutMapping("/me/password")
public ResponseEntity<?> updatePassword(@RequestBody UpdatePasswordRequest request) {
    try {
        String email = getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Validar contraseña actual
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body(Map.of(
                "ncode", 0,
                "message", "La contraseña actual es incorrecta."
            ));
        }

        // Validar que la nueva contraseña no sea igual a la actual
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body(Map.of(
                "ncode", 0,
                "message", "La nueva contraseña no puede ser igual a la actual."
            ));
        }

        // Cambiar la contraseña y guardar
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
            "ncode", 1,
            "message", "Contraseña actualizada correctamente."
        ));
    } catch (Exception e) {
        return handleException(e);
    }
}
}