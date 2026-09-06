package com.nhuamani.auth_system_spring.auth.security;

import com.nhuamani.auth_system_spring.auth.entity.RefreshToken;
import com.nhuamani.auth_system_spring.auth.entity.User;
import com.nhuamani.auth_system_spring.auth.repository.RefreshTokenRepository;
import com.nhuamani.auth_system_spring.auth.exception.RefreshTokenNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public RefreshToken createRefreshToken(User user) {

        refreshTokenRepository.deleteByUser(user);

        return generateRefreshToken(user);
    }

    private RefreshToken generateRefreshToken(User user) {

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
                        new RefreshTokenNotFoundException(
                                "Refresh token inválido o revocado"
                        )
                );
    }

    @Transactional
    public RefreshToken rotateRefreshToken(String token) {

        RefreshToken currentToken = findByToken(token);
        verifyExpiration(currentToken);
        User user = currentToken.getUser();

        // Revocar el token utilizado
        refreshTokenRepository.delete(currentToken);

        // Crear un nuevo refresh token
        return generateRefreshToken(user);
    }

    private void verifyExpiration(RefreshToken refreshToken) {

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {

            refreshTokenRepository.delete(refreshToken);

            throw new RefreshTokenNotFoundException(
                    "Refresh token expirado"
            );
        }
    }

    @Transactional
    public void revokeToken(String token) {

        refreshTokenRepository.findByToken(token)
                .ifPresent(refreshTokenRepository::delete);
    }
}
