package com.example.rentitbackend.controller;

import com.example.rentitbackend.dto.like.response.FavoriteResponse;
import com.example.rentitbackend.entity.Favorite;
import com.example.rentitbackend.entity.Member;
import com.example.rentitbackend.entity.Product;
import com.example.rentitbackend.security.UserAuthorize;
import com.example.rentitbackend.service.FavoriteService;
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

@Tag(name = "로그인 후 사용할 수 있는 API")
@UserAuthorize
@RequiredArgsConstructor
@RestController
@RequestMapping("/favorite")
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final MemberService memberService;

    @Operation(summary = "찜 추가")
    @PostMapping("/add")
    @UserAuthorize
    public ResponseEntity<Favorite> addFav(@AuthenticationPrincipal User user, @RequestParam Long productId) {
        Favorite favorite = favoriteService.addFav(UUID.fromString(user.getUsername()), productId);
        return new ResponseEntity<>(favorite, HttpStatus.CREATED);
    }

    @Operation(summary = "찜 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFav(@PathVariable Long id) {
        favoriteService.deleteFav(id);
        return ResponseEntity.noContent().build();
    }

//    @DeleteMapping("/user/{userNickname}/product/{productId}")
//    public ResponseEntity<Void> deleteFavorite(@AuthenticationPrincipal User user,
//                                               @PathVariable String userNickname,
//                                               @PathVariable Long productId) {
//        favoriteService.deleteFavorite(userNickname, productId);
//        return ResponseEntity.noContent().build();
//    }

//    @DeleteMapping("/product/{productId}")
//    public ResponseEntity<Void> deleteFavorite(@AuthenticationPrincipal User user,
//                                               @PathVariable Long productId) {
//        favoriteService.deleteFavorite(UUID.fromString(user.getUsername()), productId);
//        return ResponseEntity.noContent().build();
//    }


//@DeleteMapping("/product/{productId}")
//public ResponseEntity<Void> deleteFavorite(@AuthenticationPrincipal User user,
//                                           @PathVariable Long productId) {
//    // 현재 인증된 사용자의 정보를 가져옵니다.
//    Member member = memberService.MemberByUsername(user.getUsername());
//
//    // productId에 해당하는 Product 객체를 가져옵니다.
//    Product product = productService.findProductById(productId);
//
//    // FavoriteService를 통해 Favorite 삭제를 수행합니다.
//    favoriteService.deleteFavorite(member, product);
//
//    // 삭제 성공 응답을 반환합니다.
//    return ResponseEntity.noContent().build();
//}

    @Operation(summary = "찜 목록 가져오기")
    @GetMapping("/user/{userNickname}")
    public ResponseEntity<List<FavoriteResponse>> getFavoritesByMemberNickname(@PathVariable String userNickname) {
        List<FavoriteResponse> favoriteDTOs = favoriteService.getFavoritesByMemberNickname(userNickname);
        return ResponseEntity.ok(favoriteDTOs);
    }

    @Operation(summary = "상품 유저 찜 확인")
    @GetMapping("/check")
    public ResponseEntity<Boolean> checkFavorite(@RequestParam String memberNickname, @RequestParam Long productId) {
        boolean isFavorited = favoriteService.isProductFavoritedByUser(memberNickname, productId);
        return ResponseEntity.ok(isFavorited);
    }

    @Operation(summary = "상품 유저 찜 아이디 조회")
    @GetMapping("/product/{productId}")
    public ResponseEntity<Long> findFavoriteIdByUserIdAndProductId(@AuthenticationPrincipal User user, @PathVariable Long productId) {
        UUID userId = UUID.fromString(user.getUsername());
        Long favoriteId = favoriteService.findFavoriteIdByUserIdAndProductId(userId, productId);
        if (favoriteId != null) {
            return new ResponseEntity<>(favoriteId, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
