package com.example.rentitbackend.repository;

import com.example.rentitbackend.entity.KeywordNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface KeywordNotificationRepository extends JpaRepository<KeywordNotification, Long> {
    List<KeywordNotification> findByMemberId(UUID memberId);
}
