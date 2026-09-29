package com.mams.mams_backend.dto;

import com.mams.mams_backend.entity.Asset;

public record InventoryDTO(Long id, Long baseId, String baseName, Long equipmentTypeId, String equipmentType,
                           Integer availableQuantity) {
    public static InventoryDTO from(Asset a) {
        return new InventoryDTO(a.getId(), a.getBase().getId(), a.getBase().getName(),
                a.getEquipmentType().getId(), a.getEquipmentType().getName(), a.getQuantity());
    }
}
