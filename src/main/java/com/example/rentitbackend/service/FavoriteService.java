package com.example.rentitbackend.service;

import com.example.rentitbackend.dto.like.response.FavoriteResponse;
import com.example.rentitbackend.dto.product.response.ProductListResponse;
import com.example.rentitbackend.entity.Favorite;
import com.example.rentitbackend.entity.Member;
import com.example.rentitbackend.entity.Product;
import com.example.rentitbackend.entity.ProductImage;
import com.example.rentitbackend.repository.FavoriteRepository;
import com.example.rentitbackend.repository.MemberRepository;
import com.example.rentitbackend.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    @Autowired
    public FavoriteService(FavoriteRepository favoriteRepository, MemberRepository memberRepository, ProductRepository productRepository) {
        this.favoriteRepository = favoriteRepository;
        this.memberRepository = memberRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Favorite addFav(UUID memberId, Long productId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID에 해당하는 회원을 찾을 수 없습니다: " + memberId));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID에 해당하는 회원을 찾을 수 없습니다: " + productId));
        Favorite favorite = new Favorite();
        favorite.setMember(member);
        favorite.setProduct(product);
        return favoriteRepository.save(favorite);
    }

//    @Transactional
//    public void deleteFavorite(String userNickname, Long productId) {
//        favoriteRepository.deleteByUserNicknameAndProductId(userNickname, productId);
//    }


//    @Transactional
//    public void deleteFavorite(UUID userId, Long productId) {
//        favoriteRepository.deleteByUserIdAndProductId(userId, productId);
//    }

    @Transactional
    public void deleteFav(Long favId) {
        favoriteRepository.deleteById(favId);
    }

    @Transactional
    public List<FavoriteResponse> getFavoritesByMemberNickname(String userNickname) {
        List<Favorite> favorites = favoriteRepository.findFavoritesWithProductByMemberNickname(userNickname);
        return favorites.stream().map(favorite -> {
            Product product = favorite.getProduct();
            return new FavoriteResponse(
                    favorite.getId(),
                    new ProductListResponse(
                            product.getId(),
                            product.getTitle(),
                            product.getCategory(),
                            product.getDuration(),
                            product.getPrice(),
                            product.getImages().stream().map(ProductImage::getStoreFileName).collect(Collectors.toList())
                    )
            );
        }).collect(Collectors.toList());
    }

    public boolean isProductFavoritedByUser(String memberNickname, Long productId) {
        return favoriteRepository.existsByMemberNicknameAndProductId(memberNickname, productId);
    }

    public Long findFavoriteIdByUserIdAndProductId(UUID userId, Long productId) {
        Favorite favorite = favoriteRepository.findByMemberIdAndProductId(userId, productId);
        return favorite != null ? favorite.getId() : null;
    }

//    public List<FavoriteResponse> getFavoritesByMemberNickname(String userNickname) {
//        List<Favorite> favorites = favoriteRepository.findFavoritesWithProductByMemberNickname(userNickname);
//        return favorites.stream().map(favorite -> {
//            Product product = favorite.getProduct();
//            return new FavoriteResponse(
//                    favorite.getId(),
//                    new ProductDetailResponse(
//                            product.getTitle(),
//                            product.getCategory(),
//                            product.getDuration(),
//                            product.getPrice(),
//                            product.getDescription(),
//                            product.getImages().stream().map(ProductImage::getStoreFileName).collect(Collectors.toList()),
//                            product.getSeller().getId(),
//                            product.getSeller().getNickname(),
//                            product.getLocation()
//                    )
//            );
//        }).collect(Collectors.toList());
//    }
}
