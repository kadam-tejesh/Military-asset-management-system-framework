package com.mams.mams_backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AssignmentRequest(
        Long baseId,
        @NotNull Long equipmentTypeId,
        @NotBlank String personnelName,
        @NotNull @Min(1) Integer quantity,
        LocalDate assignedDate) {}
