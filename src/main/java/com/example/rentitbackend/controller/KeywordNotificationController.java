package com.example.rentitbackend.controller;

import com.example.rentitbackend.dto.keywordnofification.KeywordNotificationResponse;
import com.example.rentitbackend.entity.KeywordNotification;
import com.example.rentitbackend.repository.KeywordNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.userdetails.User;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@RestController
@RequestMapping("/keyword-notification")
public class KeywordNotificationController {
    @Autowired
    private KeywordNotificationRepository keywordNotificationRepository;

    @GetMapping
    public ResponseEntity<List<KeywordNotificationResponse>> getNotifications(@AuthenticationPrincipal User user) {
        UUID memberId = UUID.fromString(user.getUsername());
        List<KeywordNotification> notifications = keywordNotificationRepository.findByMemberId(memberId);
        // Convert to DTO
        List<KeywordNotificationResponse> responses = notifications.stream()
                .map(notification -> new KeywordNotificationResponse(
                        notification.getId(),
                        notification.getType(),
                        notification.getMessage(),
                        notification.getProductId(),
                        notification.getKeyword(),
                        notification.getCreatedAt()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
}
