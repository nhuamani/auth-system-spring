package com.nhuamani.auth_system_spring.auth.service;

import com.nhuamani.auth_system_spring.auth.dto.auth.RegisterRequest;
import com.nhuamani.auth_system_spring.auth.dto.auth.RegisterResponse;
import com.nhuamani.auth_system_spring.auth.entity.Role;
import com.nhuamani.auth_system_spring.auth.entity.User;
import com.nhuamani.auth_system_spring.auth.exception.EmailAlreadyExistsException;
import com.nhuamani.auth_system_spring.auth.exception.RoleNotFoundException;
import com.nhuamani.auth_system_spring.auth.repository.RoleRepository;
import com.nhuamani.auth_system_spring.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {

        String email = request.email()
                .trim()
                .toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(
                    "El email ya está registrado"
            );
        }

        Role role = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() ->
                        new RoleNotFoundException(
                                "El rol ROLE_USER no existe"
                        )
                );

        User user = User.builder()
                .name(request.name().trim())
                .email(email)
                .password(passwordEncoder.encode(request.password()))
                .role(role)
                .enabled(true)
                .build();

        User savedUser = userRepository.save(user);

        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole().getName()
        );
    }
}