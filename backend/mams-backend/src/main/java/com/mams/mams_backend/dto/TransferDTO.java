package com.mams.mams_backend.dto;

import com.mams.mams_backend.entity.Transfer;

import java.time.LocalDateTime;

public record TransferDTO(Long id, Long equipmentTypeId, String equipmentType, Integer quantity,
                          Long sourceBaseId, String sourceBase, Long destinationBaseId, String destinationBase,
                          LocalDateTime transferDate, String status, String createdBy) implements HasId {
    public static TransferDTO from(Transfer t) {
        return new TransferDTO(t.getId(), t.getEquipmentType().getId(), t.getEquipmentType().getName(),
                t.getQuantity(), t.getSourceBase().getId(), t.getSourceBase().getName(),
                t.getDestinationBase().getId(), t.getDestinationBase().getName(),
                t.getTransferDate(), t.getStatus().name(), t.getCreatedBy().getUsername());
    }
}
