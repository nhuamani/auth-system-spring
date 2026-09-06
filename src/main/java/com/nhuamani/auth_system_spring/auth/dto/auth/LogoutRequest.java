package com.nhuamani.auth_system_spring.auth.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record LogoutRequest(

        @NotBlank(message = "El refresh token es obligatorio")
        String refreshToken

) {
}