package com.fiscora.dto.auth;

public record AuthResponse(
        String token,
        String username,
        String email,
        String role
) {
}