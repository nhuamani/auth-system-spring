package com.nhuamani.auth_system_spring.auth.controller;

import com.nhuamani.auth_system_spring.auth.dto.auth.AuthResponse;
import com.nhuamani.auth_system_spring.auth.dto.auth.LoginRequest;
import com.nhuamani.auth_system_spring.auth.dto.auth.RegisterRequest;
import com.nhuamani.auth_system_spring.auth.dto.auth.RegisterResponse;
import com.nhuamani.auth_system_spring.auth.service.AuthService;
import com.nhuamani.auth_system_spring.auth.dto.RefreshTokenRequest;
import com.nhuamani.auth_system_spring.auth.dto.RefreshTokenResponse;
import com.nhuamani.auth_system_spring.auth.dto.auth.LogoutRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {

        RegisterResponse response = authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {

        AuthResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Valid @RequestBody LogoutRequest request
    ) {

        authService.logout(request.refreshToken());

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request
    ) {

        return ResponseEntity.ok(
                authService.refreshToken(request.refreshToken())
        );
    }
}