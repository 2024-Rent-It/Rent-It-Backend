package com.example.rentitbackend.repository;

import com.example.rentitbackend.entity.Member;
import com.example.rentitbackend.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByMember(Member member);

    void deleteByMember(Member member);

    Optional<Token> findByTokenAndMember(String token, Member member);
}
