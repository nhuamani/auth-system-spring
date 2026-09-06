package com.nhuamani.auth_system_spring.auth.repository;

import com.nhuamani.auth_system_spring.auth.entity.RefreshToken;
import com.nhuamani.auth_system_spring.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(User user);

    void deleteByToken(String token);
}