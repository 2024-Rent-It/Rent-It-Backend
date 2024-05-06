package com.example.rentitbackend.repository;

import com.example.rentitbackend.entity.Like;
import com.example.rentitbackend.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long> {
//    List<Like> findByMemberId(Long memberId);
    List<Like> findByMember(Member member);

    @Query("SELECT l FROM Like l JOIN FETCH l.product WHERE l.member.id = :userId")
    List<Like> findLikesWithProductByMemberId(Long userId);
}