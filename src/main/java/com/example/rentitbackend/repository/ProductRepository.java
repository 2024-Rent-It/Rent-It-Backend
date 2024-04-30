package com.example.rentitbackend.repository;

import com.example.rentitbackend.entity.Member;
import com.example.rentitbackend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByLocation(String location);
    List<Product> findBySeller(Member seller);
    List<Product> findByTitleContaining(String keyword);

    @Query("SELECT p FROM Product p ORDER BY p.createdAt DESC") // created_at 기준으로 내림차순 정렬
    List<Product> findAllOrderedByCreatedAtDesc();
}