package com.eventtickets.eventtickets.security;


public record RegisterRequest(
        String name,
        String email,
        String password
) {
}