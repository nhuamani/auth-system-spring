package com.nhuamani.auth_system_spring.auth.dto.auth;

public record RegisterResponse(
        Long id,
        String name,
        String email,
        String role
) {
}