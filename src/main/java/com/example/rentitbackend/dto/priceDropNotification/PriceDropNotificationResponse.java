package com.example.rentitbackend.dto.priceDropNotification;

import java.time.LocalDateTime;

public record PriceDropNotificationResponse(
        Long id,
        String type,
        String message,
        Long productId,
        int priceDropAmount,
        LocalDateTime createdAt
) {
}
