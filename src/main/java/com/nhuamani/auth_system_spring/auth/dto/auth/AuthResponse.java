package com.nhuamani.auth_system_spring.auth.dto.auth;

public record AuthResponse(
        Long id,
        String name,
        String email,
        String role,
        String accessToken,
        String refreshToken,
        String tokenType
) {
}