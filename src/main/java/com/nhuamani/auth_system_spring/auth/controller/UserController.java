package com.nhuamani.auth_system_spring.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

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