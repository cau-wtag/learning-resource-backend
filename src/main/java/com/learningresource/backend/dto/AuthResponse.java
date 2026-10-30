package com.learningresource.backend.dto;

public record AuthResponse(
    Long id,
    String username,
    String email
) {}