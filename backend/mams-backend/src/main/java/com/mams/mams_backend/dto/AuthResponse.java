package com.mams.mams_backend.dto;

public record AuthResponse(String token, String username, String role, Long baseId, String baseName) {}
