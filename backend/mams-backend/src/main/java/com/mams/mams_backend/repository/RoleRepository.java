package com.mams.mams_backend.repository;

import com.mams.mams_backend.entity.Role;
import com.mams.mams_backend.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}