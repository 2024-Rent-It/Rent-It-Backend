package com.example.rentitbackend.controller;

import com.example.rentitbackend.dto.like.KeywordResponse;
import com.example.rentitbackend.entity.Keyword;
import com.example.rentitbackend.security.UserAuthorize;
import com.example.rentitbackend.service.KeywordService;
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
@RequestMapping("/keyword")
public class KeywordController {

    private final KeywordService keywordService;

    @Operation(summary = "키워드 추가")
    @PostMapping("/add")
    @UserAuthorize
    public ResponseEntity<Keyword> addKeyword(@AuthenticationPrincipal User user, @RequestParam String name) {
        Keyword keyword = keywordService.addKeyword(UUID.fromString(user.getUsername()), name);
        return new ResponseEntity<>(keyword, HttpStatus.CREATED);
    }

    @Operation(summary = "키워드 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteKeyword(@PathVariable Long id) {
        keywordService.deleteKeyword(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "멤버별 키워드 목록 조회(닉네임으로)")
    @GetMapping("/user/{nickname}")
    public List<KeywordResponse> getKeywordsByMemberNickname(@PathVariable String nickname) {
        return keywordService.getKeywordsByMemberNickname(nickname);
    }


}
