package com.mams.mams_backend.controller;

import com.mams.mams_backend.dto.PageResponse;
import com.mams.mams_backend.dto.PurchaseDTO;
import com.mams.mams_backend.dto.PurchaseRequest;
import com.mams.mams_backend.service.PurchaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','LOGISTICS_OFFICER')")
    public ResponseEntity<PurchaseDTO> create(@Valid @RequestBody PurchaseRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(purchaseService.create(req));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','BASE_COMMANDER','LOGISTICS_OFFICER')")
    public PageResponse<PurchaseDTO> list(
            @RequestParam(required = false) Long baseId,
            @RequestParam(required = false) Long equipmentTypeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return purchaseService.list(baseId, equipmentTypeId, from, to, PageRequest.of(page, size));
    }
}
