package com.example.rentitbackend.dto.keywordNotification;

import java.time.LocalDateTime;

public record KeywordNotificationResponse(
        Long id,
        String type,
        String message,
        Long productId,
        String keyword,
        LocalDateTime createdAt
) {
}
