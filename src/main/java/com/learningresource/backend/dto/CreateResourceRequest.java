package com.learningresource.backend.dto;

public record CreateResourceRequest(
    String title,
    Integer userId,
    String link
) {}