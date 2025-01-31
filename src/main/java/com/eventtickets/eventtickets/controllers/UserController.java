package com.eventtickets.eventtickets.controllers;

import com.eventtickets.eventtickets.model.User;
import com.eventtickets.eventtickets.model.Role;
import com.eventtickets.eventtickets.repositories.UserRepository;
import com.eventtickets.eventtickets.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    // 🔹 Endpoint para que un ADMIN cree un usuario con cualquier rol
    @PostMapping("/admin/create-user")
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody Map<String, String> userData) {
        Map<String, Object> response = new HashMap<>();

        String username = userData.get("username");
        String email = userData.get("email");
        String password = userData.get("password");
        String roleName = userData.get("role"); // Rol enviado en la solicitud

        Optional<Role> role = roleRepository.findByName(roleName.toUpperCase());
        if (role.isEmpty()) {
            response.put("ncode", 0);
            response.put("message", "Rol no válido.");
            return ResponseEntity.ok(response);
        }

        if (userRepository.findByEmail(email).isPresent()) {
            response.put("ncode", 2);
            response.put("message", "No se pudo completar el registro. Email ya existe.");
            return ResponseEntity.ok(response);
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role.get());

        User savedUser = userRepository.save(user);
        response.put("ncode", 1);
        response.put("message", "Usuario creado exitosamente.");
        response.put("userId", savedUser.getId());
        response.put("role", role.get().getName());

        return ResponseEntity.status(201).body(response);
    }
}
