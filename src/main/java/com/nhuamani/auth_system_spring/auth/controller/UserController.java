package com.nhuamani.auth_system_spring.auth.controller;

import com.nhuamani.auth_system_spring.auth.service.UserService;
import com.nhuamani.auth_system_spring.auth.dto.user.UserProfileResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUser(
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                userService.getCurrentUser(
                        authentication.getName()
                )
        );
    }

    @GetMapping("/test")
    public ResponseEntity<?> testUser(Authentication authentication) {

        return ResponseEntity.ok(
                Map.of(
                        "message", "Acceso permitido",
                        "email", authentication.getName(),
                        "authorities", authentication.getAuthorities()
                )
        );
    }
}
