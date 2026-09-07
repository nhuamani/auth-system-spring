package com.nhuamani.auth_system_spring.auth.service;

import com.nhuamani.auth_system_spring.auth.dto.user.UserProfileResponse;
import com.nhuamani.auth_system_spring.auth.entity.User;
import com.nhuamani.auth_system_spring.auth.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserProfileResponse getCurrentUser(String email) {

        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Usuario no encontrado"
                        )
                );

        return new UserProfileResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().getName(),
                user.isEnabled()
        );
    }
}