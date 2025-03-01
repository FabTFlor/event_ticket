package com.eventtickets.eventtickets.user;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String name,
        String email,
        String role,
        String dni,
        String phoneNumber,
        LocalDateTime createdAt
) {
}
