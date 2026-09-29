package com.mams.mams_backend.service;

import com.mams.mams_backend.aspect.Audited;
import com.mams.mams_backend.dto.PageResponse;
import com.mams.mams_backend.dto.TransferDTO;
import com.mams.mams_backend.dto.TransferRequest;
import com.mams.mams_backend.entity.Base;
import com.mams.mams_backend.entity.EquipmentType;
import com.mams.mams_backend.entity.Transfer;
import com.mams.mams_backend.enums.TransferStatus;
import com.mams.mams_backend.exception.BadRequestException;
import com.mams.mams_backend.exception.NotFoundException;
import com.mams.mams_backend.repository.BaseRepository;
import com.mams.mams_backend.repository.EquipmentTypeRepository;
import com.mams.mams_backend.repository.TransferRepository;
import com.mams.mams_backend.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final TransferRepository transferRepository;
    private final BaseRepository baseRepository;
    private final EquipmentTypeRepository equipmentTypeRepository;
    private final InventoryService inventoryService;
    private final SecurityUtil securityUtil;

    @Transactional
    @Audited(action = "CREATE_TRANSFER", entityType = "Transfer")
    public TransferDTO create(TransferRequest req) {
        Long sourceId = securityUtil.resolveBaseIdForWrite(req.sourceBaseId());
        if (sourceId.equals(req.destinationBaseId())) {
            throw new BadRequestException("Source and destination base cannot be the same");
        }
        Base source = baseRepository.findById(sourceId)
                .orElseThrow(() -> new NotFoundException("Source base not found"));
        Base dest = baseRepository.findById(req.destinationBaseId())
                .orElseThrow(() -> new NotFoundException("Destination base not found"));
        EquipmentType type = equipmentTypeRepository.findById(req.equipmentTypeId())
                .orElseThrow(() -> new NotFoundException("Equipment type not found"));

        // both stock updates happen in this one transaction: all-or-nothing
        inventoryService.removeStock(source, type, req.quantity());
        inventoryService.addStock(dest, type, req.quantity());

        Transfer t = new Transfer();
        t.setSourceBase(source);
        t.setDestinationBase(dest);
        t.setEquipmentType(type);
        t.setQuantity(req.quantity());
        t.setStatus(TransferStatus.COMPLETED);
        t.setCreatedBy(securityUtil.currentUser());
        return TransferDTO.from(transferRepository.save(t));
    }

    @Transactional(readOnly = true)
    public PageResponse<TransferDTO> history(Long baseId, Long typeId, LocalDate from, LocalDate to, Pageable pageable) {
        Long scopedBase = securityUtil.resolveBaseId(baseId);
        return PageResponse.of(transferRepository
                .findHistory(scopedBase, typeId,
                        (from != null ? from : PurchaseService.MIN_DATE).atStartOfDay(),
                        (to != null ? to : PurchaseService.MAX_DATE).plusDays(1).atStartOfDay(),
                        pageable)
                .map(TransferDTO::from));
    }
}