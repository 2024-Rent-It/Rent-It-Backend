package com.example.rentitbackend.repository;

import com.example.rentitbackend.common.MemberType;
import com.example.rentitbackend.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MemberRepository extends JpaRepository<Member, UUID> {
    Optional<Member> findByAccount(String account);
    Optional<Member> findByEmail(String email);

//    Optional<Member> findById(UUID id);

    Optional<Member> findByNickname(String nickname);

    List<Member> findAllByType(MemberType type);

    boolean existsByEmail(String email);

    boolean existsByAccount(String account);

    boolean existsByNickname(String nickname);


}
