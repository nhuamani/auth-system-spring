package com.nhuamani.auth_system_spring.auth.service;

import com.nhuamani.auth_system_spring.auth.dto.auth.AuthResponse;
import com.nhuamani.auth_system_spring.auth.dto.auth.LoginRequest;
import com.nhuamani.auth_system_spring.auth.dto.auth.RegisterRequest;
import com.nhuamani.auth_system_spring.auth.dto.auth.RegisterResponse;
import com.nhuamani.auth_system_spring.auth.entity.Role;
import com.nhuamani.auth_system_spring.auth.entity.User;
import com.nhuamani.auth_system_spring.auth.exception.EmailAlreadyExistsException;
import com.nhuamani.auth_system_spring.auth.exception.RoleNotFoundException;
import com.nhuamani.auth_system_spring.auth.repository.RoleRepository;
import com.nhuamani.auth_system_spring.auth.repository.UserRepository;
import com.nhuamani.auth_system_spring.auth.security.JwtService;
import com.nhuamani.auth_system_spring.auth.security.RefreshTokenService;
import com.nhuamani.auth_system_spring.auth.dto.RefreshTokenResponse;
import com.nhuamani.auth_system_spring.auth.entity.RefreshToken;
import com.nhuamani.auth_system_spring.auth.security.CustomUserDetailsService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final CustomUserDetailsService userDetailsService;

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

    @Transactional
    public AuthResponse login(LoginRequest request) {

        String email = request.email()
                .trim()
                .toLowerCase();

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                email,
                                request.password()
                        )
                );

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Usuario no encontrado"
                        )
                );

        String accessToken = jwtService.generateToken(
                (UserDetails) authentication.getPrincipal()
        );

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return new AuthResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().getName(),
                accessToken,
                refreshToken.getToken(),
                "Bearer"
        );
    }

    @Transactional
    public RefreshTokenResponse refreshToken(String token) {

        RefreshToken newRefreshToken = refreshTokenService.rotateRefreshToken(token);

        User user = newRefreshToken.getUser();

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

        String accessToken = jwtService.generateToken(userDetails);

        return new RefreshTokenResponse(
                accessToken,
                newRefreshToken.getToken(),
                "Bearer"
        );
    }

    public void logout(String refreshToken) {

        refreshTokenService.revokeToken(refreshToken);
    }
}