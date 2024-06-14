package com.example.rentitbackend.repository;

import com.example.rentitbackend.entity.Message;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class MessageRepository {
    private final EntityManager em;

    public void save(Message message) {
        em.persist(message);
    }
}
