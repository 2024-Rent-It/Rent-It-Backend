package com.example.rentitbackend.dto.like;

import java.time.LocalDateTime;

public record KeywordResponse(
        Long id,
        String name,
        LocalDateTime createdAt
) {}