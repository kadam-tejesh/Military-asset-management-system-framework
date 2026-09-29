package com.mams.mams_backend.dto;

import com.mams.mams_backend.entity.User;

public record UserDTO(Long id, String username, String email, String role, Long baseId, String baseName, boolean enabled)
        implements HasId {
    public static UserDTO from(User u) {
        return new UserDTO(u.getId(), u.getUsername(), u.getEmail(), u.getRole().getName().name(),
                u.getBase() != null ? u.getBase().getId() : null,
                u.getBase() != null ? u.getBase().getName() : null,
                u.isEnabled());
    }
}
