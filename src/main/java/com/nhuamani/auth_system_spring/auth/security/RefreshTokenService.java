package com.nhuamani.auth_system_spring.auth.security;

import com.nhuamani.auth_system_spring.auth.entity.RefreshToken;
import com.nhuamani.auth_system_spring.auth.entity.User;
import com.nhuamani.auth_system_spring.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    private final SecureRandom secureRandom = new SecureRandom();

    public RefreshToken createRefreshToken(User user) {

        // Eliminar el refresh token anterior
        refreshTokenRepository.deleteByUser(user);

        byte[] randomBytes = new byte[64];
        secureRandom.nextBytes(randomBytes);

        String token = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .user(user)
                .expiresAt(
                        LocalDateTime.now()
                                .plusSeconds(refreshTokenExpiration / 1000)
                )
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken findByToken(String token) {

        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() ->
                        new RuntimeException("Refresh token no encontrado")
                );
    }

    public RefreshToken verifyExpiration(RefreshToken refreshToken) {

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {

            refreshTokenRepository.delete(refreshToken);

            throw new RuntimeException("Refresh token expirado");
        }

        return refreshToken;
    }

    public void deleteByToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }
}