package com.example.rentitbackend.repository;

import com.example.rentitbackend.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
}
