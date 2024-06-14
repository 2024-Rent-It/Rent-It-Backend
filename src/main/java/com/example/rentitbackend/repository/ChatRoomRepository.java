package com.example.rentitbackend.repository;

import com.example.rentitbackend.entity.ChatRoom;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ChatRoomRepository {
    private final EntityManager em;

    public Long save(ChatRoom chatRoom) {
        em.persist(chatRoom);
        return chatRoom.getId();
    }

    public Optional<ChatRoom> findById(Long id) {
        return Optional.ofNullable(em.find(ChatRoom.class, id));
    }


    public Optional<ChatRoom> findByMemberAndProduct(UUID customerId, UUID sellerId, Long productId) {
        return em.createQuery("select r from ChatRoom r where r.buyer.id = :buyerId and r.seller.id = :sellerId and r.product.id = :productId", ChatRoom.class)
                .setParameter("buyerId", customerId)
                .setParameter("sellerId", sellerId)
                .setParameter("productId", productId)
                .getResultList().stream().findFirst();
    }

    public List<ChatRoom> findListByMemberId(UUID id) {
        return em.createQuery("select r from ChatRoom r where r.buyer.id = :id or r.seller.id = :id", ChatRoom.class)
                .setParameter("id", id)
                .getResultList();
    }


    public List<String> findDistinctBuyerNicknamesByProductId(Long productId) {
        return em.createQuery("select distinct r.buyer.nickname from ChatRoom r where r.product.id = :productId", String.class)
                .setParameter("productId", productId)
                .getResultList();
    }
}
