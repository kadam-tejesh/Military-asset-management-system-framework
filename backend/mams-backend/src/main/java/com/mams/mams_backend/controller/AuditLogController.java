package com.mams.mams_backend.controller;

import com.mams.mams_backend.dto.AuditLogDTO;
import com.mams.mams_backend.dto.PageResponse;
import com.mams.mams_backend.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/audit-logs")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    public PageResponse<AuditLogDTO> list(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "20") int size) {
        return auditLogService.list(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp")));
    }
}
