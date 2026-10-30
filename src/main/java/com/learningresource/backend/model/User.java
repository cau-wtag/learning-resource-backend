package com.learningresource.backend.model;

import java.time.OffsetDateTime;

public record User(
    Long id,
    String username,
    String email,
    String passwordHash,
    OffsetDateTime createdAt
) {}