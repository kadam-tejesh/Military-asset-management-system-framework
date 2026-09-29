package com.mams.mams_backend.dto;

import com.mams.mams_backend.entity.AuditLog;

import java.time.LocalDateTime;

public record AuditLogDTO(Long id, String username, String action, String entityType, Long entityId,
                          String details, String ipAddress, LocalDateTime timestamp) {
    public static AuditLogDTO from(AuditLog l) {
        return new AuditLogDTO(l.getId(), l.getUser() != null ? l.getUser().getUsername() : null,
                l.getAction(), l.getEntityType(), l.getEntityId(), l.getDetails(),
                l.getIpAddress(), l.getTimestamp());
    }
}
