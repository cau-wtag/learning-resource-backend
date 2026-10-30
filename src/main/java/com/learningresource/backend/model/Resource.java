package com.learningresource.backend.model;

import java.time.OffsetDateTime;

public record Resource(
    Integer id,
    String title,
    Integer userId,
    OffsetDateTime createdAt
) {}