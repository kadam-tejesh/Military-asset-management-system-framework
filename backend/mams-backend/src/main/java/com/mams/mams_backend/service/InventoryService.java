package com.mams.mams_backend.service;

import com.mams.mams_backend.entity.Asset;
import com.mams.mams_backend.entity.Base;
import com.mams.mams_backend.entity.EquipmentType;
import com.mams.mams_backend.enums.AssetStatus;
import com.mams.mams_backend.exception.BadRequestException;
import com.mams.mams_backend.repository.AssetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final AssetRepository assetRepository;

    @Transactional
    public Asset addStock(Base base, EquipmentType type, int qty) {
        Asset stock = assetRepository
                .findFirstByBaseIdAndEquipmentTypeIdAndStatus(base.getId(), type.getId(), AssetStatus.AVAILABLE)
                .orElseGet(() -> {
                    Asset a = new Asset();
                    a.setBase(base);
                    a.setEquipmentType(type);
                    a.setQuantity(0);
                    a.setStatus(AssetStatus.AVAILABLE);
                    return a;
                });
        stock.setQuantity(stock.getQuantity() + qty);
        return assetRepository.save(stock);
    }

    @Transactional
    public Asset removeStock(Base base, EquipmentType type, int qty) {
        Asset stock = assetRepository
                .findFirstByBaseIdAndEquipmentTypeIdAndStatus(base.getId(), type.getId(), AssetStatus.AVAILABLE)
                .orElseThrow(() -> new BadRequestException(
                        "No " + type.getName() + " stock at " + base.getName()));
        if (stock.getQuantity() < qty) {
            throw new BadRequestException("Insufficient " + type.getName() + " at " + base.getName()
                    + " (available: " + stock.getQuantity() + ", requested: " + qty + ")");
        }
        stock.setQuantity(stock.getQuantity() - qty);
        return assetRepository.save(stock);
    }
}
