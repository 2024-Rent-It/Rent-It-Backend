package com.example.rentitbackend.repository;

import com.example.rentitbackend.entity.Favorite;
import com.example.rentitbackend.entity.Member;
import com.example.rentitbackend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
//    List<Favorite> findByMemberId(Long memberId);
    List<Favorite> findByMember(Member member);

    @Query("SELECT f FROM Favorite f JOIN FETCH f.product WHERE f.member.nickname = :userNickname")
    List<Favorite> findFavoritesWithProductByMemberNickname(String userNickname);

//    void deleteByUserNicknameAndProductId(String userNickname, Long productId);

//    void deleteByUserIdAndProductId(UUID userId, Long productId);
    boolean existsByMemberNicknameAndProductId(String memberNickname, Long productId);

    Favorite findByMemberIdAndProductId(UUID memberId, Long productId);

    List<Favorite> findByProduct(Product product);
}