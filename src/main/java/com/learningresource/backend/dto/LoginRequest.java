package com.learningresource.backend.dto;

public record LoginRequest(
    String username,
    String password
) {}