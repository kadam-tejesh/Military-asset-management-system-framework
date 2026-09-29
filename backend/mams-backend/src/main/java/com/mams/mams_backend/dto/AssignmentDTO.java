package com.mams.mams_backend.dto;

import com.mams.mams_backend.entity.Assignment;

import java.time.LocalDate;

public record AssignmentDTO(Long id, Long baseId, String baseName, Long equipmentTypeId, String equipmentType,
                            String personnelName, Integer quantity, LocalDate assignedDate, String status,
                            LocalDate expendedDate, String expendedReason, String createdBy) implements HasId {
    public static AssignmentDTO from(Assignment a) {
        return new AssignmentDTO(a.getId(), a.getBase().getId(), a.getBase().getName(),
                a.getAsset().getEquipmentType().getId(), a.getAsset().getEquipmentType().getName(),
                a.getPersonnelName(), a.getQuantity(), a.getAssignedDate(), a.getStatus().name(),
                a.getExpendedDate(), a.getExpendedReason(), a.getCreatedBy().getUsername());
    }
}
