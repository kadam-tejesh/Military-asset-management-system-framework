package com.mams.mams_backend.controller;

import com.mams.mams_backend.dto.AssignmentDTO;
import com.mams.mams_backend.dto.AssignmentRequest;
import com.mams.mams_backend.dto.ExpendRequest;
import com.mams.mams_backend.dto.PageResponse;
import com.mams.mams_backend.enums.AssignmentStatus;
import com.mams.mams_backend.service.AssignmentService;
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
@RequestMapping("/api/assignments")
@PreAuthorize("hasAnyRole('ADMIN','BASE_COMMANDER')")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;

    @PostMapping
    public ResponseEntity<AssignmentDTO> assign(@Valid @RequestBody AssignmentRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(assignmentService.assign(req));
    }

    @PostMapping("/{id}/expend")
    public AssignmentDTO expend(@PathVariable Long id, @RequestBody(required = false) ExpendRequest req) {
        return assignmentService.expend(id, req != null ? req : new ExpendRequest(null, null));
    }

    @PostMapping("/{id}/return")
    public AssignmentDTO returnAsset(@PathVariable Long id) {
        return assignmentService.returnAsset(id);
    }

    @GetMapping
    public PageResponse<AssignmentDTO> list(
            @RequestParam(required = false) Long baseId,
            @RequestParam(required = false) Long equipmentTypeId,
            @RequestParam(required = false) AssignmentStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return assignmentService.list(baseId, equipmentTypeId, status, from, to, PageRequest.of(page, size));
    }
}
