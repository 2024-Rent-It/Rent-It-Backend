package com.example.rentitbackend.repository;

import com.example.rentitbackend.common.DealStatus;
import com.example.rentitbackend.entity.Member;
import com.example.rentitbackend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
//    List<Product> findByLocation(String location);
    List<Product> findAllByLocationOrderByCreatedAtDesc(String location);
    List<Product> findAllByLocationAndCategoryOrderByCreatedAtDesc(String location, String category);
//    List<Product> findBySeller(Member seller);
    List<Product> findBySellerOrderByCreatedAtDesc(Member seller);
//    List<Product> findByBuyer(Member buyer);
    List<Product> findByBuyerOrderByCreatedAtDesc(Member buyer);
    List<Product> findByTitleContaining(String keyword);

    @Query("SELECT p FROM Product p ORDER BY p.createdAt DESC") // created_at 기준으로 내림차순 정렬
    List<Product> findAllOrderedByCreatedAtDesc();

    List<Product> findAllByTitleContainingAndLocationOrderByCreatedAtDesc(String searchKeyword, String location);//최신순 검색

    List<Product> findAllByTitleContainingAndLocationOrderByPriceDesc(String searchKeyword, String location); //고가순 검색

    List<Product> findAllByTitleContainingAndLocationOrderByPriceAsc(String searchKeyword, String location); //저가순 검색

    List<Product> findAllBySellerAndLocationOrderByCreatedAtDesc(Member seller, String location);

    List<Product> findByLocationAndStatusOrderByCreatedAtDesc(String location, DealStatus status);

}