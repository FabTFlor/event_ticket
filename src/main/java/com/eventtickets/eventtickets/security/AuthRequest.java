package com.eventtickets.eventtickets.security;

public record AuthRequest(
        String email,
        String password
) {
}
