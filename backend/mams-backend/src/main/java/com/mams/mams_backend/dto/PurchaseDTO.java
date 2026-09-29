package com.mams.mams_backend.dto;

import com.mams.mams_backend.entity.Purchase;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PurchaseDTO(Long id, Long baseId, String baseName, Long equipmentTypeId, String equipmentType,
                          Integer quantity, LocalDate purchaseDate, String vendor, String createdBy,
                          LocalDateTime createdAt) implements HasId {
    public static PurchaseDTO from(Purchase p) {
        return new PurchaseDTO(p.getId(), p.getBase().getId(), p.getBase().getName(),
                p.getEquipmentType().getId(), p.getEquipmentType().getName(),
                p.getQuantity(), p.getPurchaseDate(), p.getVendor(),
                p.getCreatedBy().getUsername(), p.getCreatedAt());
    }
}
