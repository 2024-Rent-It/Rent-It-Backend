package com.example.rentitbackend.repository;

import com.example.rentitbackend.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;


public interface KeywordRepository extends JpaRepository<Keyword, Long> {
}
