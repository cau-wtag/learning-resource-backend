package com.learningresource.backend.dto;

public record CreateUserRequest(
    String username,
    String email,
    String password
) {}