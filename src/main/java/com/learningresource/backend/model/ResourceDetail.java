package com.learningresource.backend.model;

import java.time.OffsetDateTime;

public record ResourceDetail(
    Integer id,
    Integer resourceId,
    String link,
    OffsetDateTime createdAt
) {}