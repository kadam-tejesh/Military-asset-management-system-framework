package com.mams.mams_backend.service;

import com.mams.mams_backend.aspect.Audited;
import com.mams.mams_backend.dto.PageResponse;
import com.mams.mams_backend.dto.PurchaseDTO;
import com.mams.mams_backend.dto.PurchaseRequest;
import com.mams.mams_backend.entity.Base;
import com.mams.mams_backend.entity.EquipmentType;
import com.mams.mams_backend.entity.Purchase;
import com.mams.mams_backend.exception.NotFoundException;
import com.mams.mams_backend.repository.BaseRepository;
import com.mams.mams_backend.repository.EquipmentTypeRepository;
import com.mams.mams_backend.repository.PurchaseRepository;
import com.mams.mams_backend.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    static final LocalDate MIN_DATE = LocalDate.of(1970, 1, 1);
    static final LocalDate MAX_DATE = LocalDate.of(2999, 12, 31);

    private final PurchaseRepository purchaseRepository;
    private final BaseRepository baseRepository;
    private final EquipmentTypeRepository equipmentTypeRepository;
    private final InventoryService inventoryService;
    private final SecurityUtil securityUtil;

    @Transactional
    @Audited(action = "CREATE_PURCHASE", entityType = "Purchase")
    public PurchaseDTO create(PurchaseRequest req) {
        Long baseId = securityUtil.resolveBaseIdForWrite(req.baseId());
        Base base = baseRepository.findById(baseId)
                .orElseThrow(() -> new NotFoundException("Base not found"));
        EquipmentType type = equipmentTypeRepository.findById(req.equipmentTypeId())
                .orElseThrow(() -> new NotFoundException("Equipment type not found"));

        Purchase p = new Purchase();
        p.setBase(base);
        p.setEquipmentType(type);
        p.setQuantity(req.quantity());
        p.setPurchaseDate(req.purchaseDate() != null ? req.purchaseDate() : LocalDate.now());
        p.setVendor(req.vendor());
        p.setCreatedBy(securityUtil.currentUser());

        Purchase saved = purchaseRepository.save(p);
        inventoryService.addStock(base, type, req.quantity());
        return PurchaseDTO.from(saved);
    }

    @Transactional(readOnly = true)
    public PageResponse<PurchaseDTO> list(Long baseId, Long typeId, LocalDate from, LocalDate to, Pageable pageable) {
        Long scopedBase = securityUtil.resolveBaseId(baseId);
        return PageResponse.of(purchaseRepository
                .findFiltered(scopedBase, typeId,
                        from != null ? from : MIN_DATE, to != null ? to : MAX_DATE, pageable)
                .map(PurchaseDTO::from));
    }
}
