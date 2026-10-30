package com.learningresource.backend.model;

import java.time.OffsetDateTime;

public record ResourceSummary(
    Integer id,
    String title,
    Integer userId,
    String link,
    OffsetDateTime createdAt
) {}