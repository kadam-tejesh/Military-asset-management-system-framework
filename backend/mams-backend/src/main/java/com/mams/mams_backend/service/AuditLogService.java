package com.mams.mams_backend.service;

import com.mams.mams_backend.dto.AuditLogDTO;
import com.mams.mams_backend.dto.PageResponse;
import com.mams.mams_backend.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Transactional(readOnly = true)
    public PageResponse<AuditLogDTO> list(Pageable pageable) {
        return PageResponse.of(auditLogRepository.findAll(pageable).map(AuditLogDTO::from));
    }
}
