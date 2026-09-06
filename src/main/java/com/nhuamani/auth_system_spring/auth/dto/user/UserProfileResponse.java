package com.nhuamani.auth_system_spring.auth.dto.user;

public record UserProfileResponse(
        Long id,
        String name,
        String email,
        String role,
        boolean enabled
) {
}