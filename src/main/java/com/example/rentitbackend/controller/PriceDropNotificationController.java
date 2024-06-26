package com.example.rentitbackend.controller;

import com.example.rentitbackend.dto.priceDropNotification.PriceDropNotificationResponse;
import com.example.rentitbackend.entity.PriceDropNotification;
import com.example.rentitbackend.repository.PriceDropNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/price-drop-notification")
public class PriceDropNotificationController {
    @Autowired
    private final PriceDropNotificationRepository priceDropNotificationRepository;

    @GetMapping
    public ResponseEntity<List<PriceDropNotificationResponse>> getNotifications(@AuthenticationPrincipal User user) {
        UUID memberId = UUID.fromString(user.getUsername());
        List<PriceDropNotification> notifications = priceDropNotificationRepository.findByMemberId(memberId);
        // Convert to DTO
        List<PriceDropNotificationResponse> responses = notifications.stream()
                .map(notification -> new PriceDropNotificationResponse(
                        notification.getId(),
                        notification.getType(),
                        notification.getMessage(),
                        notification.getProductId(),
                        notification.getPriceDropAmount(),
                        notification.getCreatedAt()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
}
