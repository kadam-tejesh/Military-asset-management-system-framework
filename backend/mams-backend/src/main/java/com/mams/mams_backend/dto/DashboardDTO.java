package com.mams.mams_backend.dto;

public record DashboardDTO(long openingBalance, long closingBalance, long netMovement,
                           long purchases, long transfersIn, long transfersOut,
                           long assigned, long expended) {}