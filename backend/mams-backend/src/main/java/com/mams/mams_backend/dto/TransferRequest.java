package com.mams.mams_backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record TransferRequest(
        Long sourceBaseId,                 // required for ADMIN; forced to own base otherwise
        @NotNull Long destinationBaseId,
        @NotNull Long equipmentTypeId,
        @NotNull @Min(1) Integer quantity) {}
