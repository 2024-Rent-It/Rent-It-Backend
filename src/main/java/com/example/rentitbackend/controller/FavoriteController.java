package com.example.rentitbackend.controller;

import com.example.rentitbackend.dto.like.response.LikeListByMemberResponse;
import com.example.rentitbackend.dto.product.response.ProductListBySellerResponse;
import com.example.rentitbackend.entity.Like;
import com.example.rentitbackend.entity.Member;
import com.example.rentitbackend.entity.Product;
import com.example.rentitbackend.entity.ProductImage;
import com.example.rentitbackend.security.UserAuthorize;
import com.example.rentitbackend.service.LikeService;
import com.example.rentitbackend.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "로그인 후 사용할 수 있는 API")
@UserAuthorize
@RequiredArgsConstructor
@RestController
@RequestMapping("/like")
public class LikeController {

    private final LikeService likeService;
    private final MemberService memberService;

    @Operation(summary = "찜 추가")
    @PostMapping("/add")
    @UserAuthorize
    public ResponseEntity<Like> addLike(@AuthenticationPrincipal User user, Long productId) {
        Like like= likeService.addLike(UUID.fromString(user.getUsername()), productId);
        return new ResponseEntity<>(like, HttpStatus.CREATED);
    }

    @Operation(summary = "찜 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLike(@PathVariable Long id) {
        likeService.deleteLike(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Like>> getLikesByUserId(@PathVariable Long userId) {
        List<Like> likes = likeService.getLikesByUserId(userId);
        return ResponseEntity.ok(likes);
    }
//    @Operation(summary = "유저별 찜 상품 목록 조회")
//    @GetMapping("/{nickname}")
//    public ResponseEntity<List<LikeListByMemberResponse>> getLikesByMember(@PathVariable String nickname) {
//        Member member = memberService.findByNickname(nickname);
//        List<Like> likes = likeService.getLikesByMember(member);
//        List<LikeListByMemberResponse> likeListByMemberResponses = likes.stream().map(product -> {
//            LikeListByMemberResponse response = new LikeListByMemberResponse(
//                    product.getTitle(),
//                    product.getCategory(),
//                    product.getDuration(),
//                    product.getPrice(),
//                    product.getDescription(),
//                    product.getImages().stream().map(ProductImage::getStoreFileName).collect(Collectors.toList()),
//                    product.getSeller().getId(),
//                    product.getSeller().getNickname(),
//                    product.getLocation(),
//                    product.getStatus()
//            );
//            return response;
//        }).collect(Collectors.toList());
//        return ResponseEntity.ok(productResponseList);
////        return ResponseEntity.ok(products);
//    }

}
