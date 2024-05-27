package com.example.rentitbackend.repository;

import com.example.rentitbackend.entity.Keyword;
import com.example.rentitbackend.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    List<Keyword> findByMember(Member member);
    List<Keyword> findByName(String keywordName);
}
