package com.example.rentitbackend.service;

import com.example.rentitbackend.entity.Like;
import com.example.rentitbackend.entity.Member;
import com.example.rentitbackend.entity.Product;
import com.example.rentitbackend.repository.LikeRepository;
import com.example.rentitbackend.repository.MemberRepository;
import com.example.rentitbackend.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    @Autowired
    public LikeService(LikeRepository likeRepository, MemberRepository memberRepository, ProductRepository productRepository) {
        this.likeRepository = likeRepository;
        this.memberRepository = memberRepository;
        this.productRepository = productRepository;
    }

    public Like addLike(UUID memberId, Long productId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID에 해당하는 회원을 찾을 수 없습니다: " + memberId));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID에 해당하는 회원을 찾을 수 없습니다: " + productId));
        Like like = new Like();
        like.setMember(member);
        like.setProduct(product);
        return likeRepository.save(like);
    }

    public void deleteLike(Long likeId) {
        likeRepository.deleteById(likeId);
    }

    public List<Like> getLikesByUserId(Long userId) {
        return likeRepository.findLikesWithProductByMemberId(userId);
    }

//    public List<Like> getLikesByMember(Member member) {
//        return likeRepository.findByMember(member);
//    }
}
