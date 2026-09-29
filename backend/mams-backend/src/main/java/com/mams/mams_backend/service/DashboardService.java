package com.mams.mams_backend.service;

import com.mams.mams_backend.dto.DashboardDTO;
import com.mams.mams_backend.dto.NetMovementDetailDTO;
import com.mams.mams_backend.dto.PurchaseDTO;
import com.mams.mams_backend.dto.TransferDTO;
import com.mams.mams_backend.repository.AssignmentRepository;
import com.mams.mams_backend.repository.PurchaseRepository;
import com.mams.mams_backend.repository.TransferRepository;
import com.mams.mams_backend.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final PurchaseRepository purchaseRepository;
    private final TransferRepository transferRepository;
    private final AssignmentRepository assignmentRepository;
    private final SecurityUtil securityUtil;

    @Transactional(readOnly = true)
    public DashboardDTO getMetrics(Long baseId, Long typeId, LocalDate from, LocalDate to) {
        Long base = securityUtil.resolveBaseId(baseId);
        LocalDate f = from != null ? from : LocalDate.now().withDayOfMonth(1);
        LocalDate t = to != null ? to : LocalDate.now();

        LocalDate min = PurchaseService.MIN_DATE;
        LocalDate dayBefore = f.minusDays(1);
        LocalDateTime minTs = min.atStartOfDay();
        LocalDateTime fStart = f.atStartOfDay();
        LocalDateTime tEnd = t.plusDays(1).atStartOfDay();

        // Opening = everything that happened before the period start
        long opening = purchaseRepository.sumBetween(base, typeId, min, dayBefore)
                + transferRepository.sumIn(base, typeId, minTs, fStart)
                - transferRepository.sumOut(base, typeId, minTs, fStart)
                - assignmentRepository.sumExpended(base, typeId, min, dayBefore);

        long purchases = purchaseRepository.sumBetween(base, typeId, f, t);
        long in = transferRepository.sumIn(base, typeId, fStart, tEnd);
        long out = transferRepository.sumOut(base, typeId, fStart, tEnd);
        long net = purchases + in - out;

        long expended = assignmentRepository.sumExpended(base, typeId, f, t);
        long assigned = assignmentRepository.sumAssigned(base, typeId, f, t);
        long closing = opening + net - expended;

        return new DashboardDTO(opening, closing, net, purchases, in, out, assigned, expended);
    }

    @Transactional(readOnly = true)
    public NetMovementDetailDTO getNetMovementDetails(Long baseId, Long typeId, LocalDate from, LocalDate to) {
        Long base = securityUtil.resolveBaseId(baseId);
        LocalDate f = from != null ? from : LocalDate.now().withDayOfMonth(1);
        LocalDate t = to != null ? to : LocalDate.now();
        LocalDateTime start = f.atStartOfDay();
        LocalDateTime end = t.plusDays(1).atStartOfDay();

        return new NetMovementDetailDTO(
                purchaseRepository.findFiltered(base, typeId, f, t, Pageable.unpaged())
                        .map(PurchaseDTO::from).getContent(),
                transferRepository.findIncoming(base, typeId, start, end).stream().map(TransferDTO::from).toList(),
                transferRepository.findOutgoing(base, typeId, start, end).stream().map(TransferDTO::from).toList());
    }
}
