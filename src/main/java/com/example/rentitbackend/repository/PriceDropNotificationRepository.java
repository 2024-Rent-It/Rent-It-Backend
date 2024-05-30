package com.example.rentitbackend.repository;

import com.example.rentitbackend.entity.PriceDropNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PriceDropNotificationRepository extends JpaRepository<PriceDropNotification, Long> {
    List<PriceDropNotification> findByMemberId(UUID memberId);
}
