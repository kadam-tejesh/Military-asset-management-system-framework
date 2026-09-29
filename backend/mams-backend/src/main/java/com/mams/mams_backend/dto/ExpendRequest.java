package com.mams.mams_backend.dto;

import java.time.LocalDate;

public record ExpendRequest(String reason, LocalDate expendedDate) {}
