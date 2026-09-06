package com.nhuamani.auth_system_spring.auth.repository;

import com.nhuamani.auth_system_spring.auth.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);
}