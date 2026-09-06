package com.nhuamani.auth_system_spring.auth.dto;

public record RefreshTokenResponse(
        String accessToken,
        String refreshToken,
        String tokenType
) {}