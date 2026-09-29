package com.mams.mams_backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PurchaseRequest(
        Long baseId,                       // required for ADMIN; ignored/forced for base-bound roles
        @NotNull Long equipmentTypeId,
        @NotNull @Min(1) Integer quantity,
        LocalDate purchaseDate,
        String vendor) {}
