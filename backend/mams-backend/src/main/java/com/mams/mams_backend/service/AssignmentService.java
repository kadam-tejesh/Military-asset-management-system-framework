package com.mams.mams_backend.service;

import com.mams.mams_backend.aspect.Audited;
import com.mams.mams_backend.dto.AssignmentDTO;
import com.mams.mams_backend.dto.AssignmentRequest;
import com.mams.mams_backend.dto.ExpendRequest;
import com.mams.mams_backend.dto.PageResponse;
import com.mams.mams_backend.entity.Asset;
import com.mams.mams_backend.entity.Assignment;
import com.mams.mams_backend.entity.Base;
import com.mams.mams_backend.entity.EquipmentType;
import com.mams.mams_backend.enums.AssignmentStatus;
import com.mams.mams_backend.exception.BadRequestException;
import com.mams.mams_backend.exception.NotFoundException;
import com.mams.mams_backend.repository.AssignmentRepository;
import com.mams.mams_backend.repository.BaseRepository;
import com.mams.mams_backend.repository.EquipmentTypeRepository;
import com.mams.mams_backend.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final BaseRepository baseRepository;
    private final EquipmentTypeRepository equipmentTypeRepository;
    private final InventoryService inventoryService;
    private final SecurityUtil securityUtil;

    @Transactional
    @Audited(action = "ASSIGN_ASSET", entityType = "Assignment")
    public AssignmentDTO assign(AssignmentRequest req) {
        Long baseId = securityUtil.resolveBaseIdForWrite(req.baseId());
        Base base = baseRepository.findById(baseId)
                .orElseThrow(() -> new NotFoundException("Base not found"));
        EquipmentType type = equipmentTypeRepository.findById(req.equipmentTypeId())
                .orElseThrow(() -> new NotFoundException("Equipment type not found"));

        Asset stock = inventoryService.removeStock(base, type, req.quantity());

        Assignment a = new Assignment();
        a.setAsset(stock);
        a.setBase(base);
        a.setPersonnelName(req.personnelName());
        a.setQuantity(req.quantity());
        a.setAssignedDate(req.assignedDate() != null ? req.assignedDate() : LocalDate.now());
        a.setStatus(AssignmentStatus.ASSIGNED);
        a.setCreatedBy(securityUtil.currentUser());
        return AssignmentDTO.from(assignmentRepository.save(a));
    }

    @Transactional
    @Audited(action = "EXPEND_ASSET", entityType = "Assignment")
    public AssignmentDTO expend(Long id, ExpendRequest req) {
        Assignment a = loadScoped(id);
        requireAssigned(a);
        a.setStatus(AssignmentStatus.EXPENDED);
        a.setExpendedDate(req.expendedDate() != null ? req.expendedDate() : LocalDate.now());
        a.setExpendedReason(req.reason());
        return AssignmentDTO.from(assignmentRepository.save(a));
    }

    @Transactional
    @Audited(action = "RETURN_ASSET", entityType = "Assignment")
    public AssignmentDTO returnAsset(Long id) {
        Assignment a = loadScoped(id);
        requireAssigned(a);
        inventoryService.addStock(a.getBase(), a.getAsset().getEquipmentType(), a.getQuantity());
        a.setStatus(AssignmentStatus.RETURNED);
        return AssignmentDTO.from(assignmentRepository.save(a));
    }

    @Transactional(readOnly = true)
    public PageResponse<AssignmentDTO> list(Long baseId, Long typeId, AssignmentStatus status,
                                            LocalDate from, LocalDate to, Pageable pageable) {
        Long scopedBase = securityUtil.resolveBaseId(baseId);
        return PageResponse.of(assignmentRepository
                .findFiltered(scopedBase, typeId, status,
                        from != null ? from : PurchaseService.MIN_DATE,
                        to != null ? to : PurchaseService.MAX_DATE, pageable)
                .map(AssignmentDTO::from));
    }

    private Assignment loadScoped(Long id) {
        Assignment a = assignmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Assignment not found"));
        securityUtil.resolveBaseId(a.getBase().getId()); // throws 403 if it belongs to another base
        return a;
    }

    private void requireAssigned(Assignment a) {
        if (a.getStatus() != AssignmentStatus.ASSIGNED) {
            throw new BadRequestException("Assignment is already " + a.getStatus());
        }
    }
}
