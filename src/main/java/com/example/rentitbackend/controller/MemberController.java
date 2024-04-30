package com.example.rentitbackend.controller;

import com.example.rentitbackend.dto.ApiResponse;
import com.example.rentitbackend.dto.member.request.MemberUpdateRequest;
import com.example.rentitbackend.security.UserAuthorize;
import com.example.rentitbackend.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "로그인 후 사용할 수 있는 API")
//@PreAuthorize("hasAuthority('USER')")
@UserAuthorize
@RequiredArgsConstructor
@RestController
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;


    @Operation(summary = "회원 정보 조회")
    @GetMapping
    public ApiResponse getMemberInfo(@AuthenticationPrincipal User user) {
        return ApiResponse.success(memberService.getMemberInfo(UUID.fromString(user.getUsername())));
    }


    @Operation(summary = "회원 탈퇴")
    @DeleteMapping
    public ApiResponse deleteMember(@AuthenticationPrincipal User user) {
        return ApiResponse.success(memberService.deleteMember(UUID.fromString(user.getUsername())));
    }

    @Operation(summary = "회원 정보 수정")
    @PutMapping
    public ApiResponse updateMember(@AuthenticationPrincipal User user, @RequestBody MemberUpdateRequest request) {
        return ApiResponse.success(memberService.updateMember(UUID.fromString(user.getUsername()), request));
    }

    @PutMapping("/update-nickname")
    public ApiResponse updateNickname(@AuthenticationPrincipal User user, @RequestParam String nickname) {
        return ApiResponse.success(memberService.updateNickname(UUID.fromString(user.getUsername()), nickname));
    }

    @PutMapping("/update-email")
    public ApiResponse updateEmail(@AuthenticationPrincipal User user, @RequestParam String email) {
        return ApiResponse.success(memberService.updateEmail(UUID.fromString(user.getUsername()), email));
    }

    @PutMapping("/update-location")
    public ApiResponse updateLocation(@AuthenticationPrincipal User user, @RequestParam String location) {
        return ApiResponse.success(memberService.updateLocation(UUID.fromString(user.getUsername()), location));
    }

//    @PutMapping("/update-password")
//    public ApiResponse updatePassword(@AuthenticationPrincipal User user, @RequestParam String currentPassword, @RequestParam String newPassword) {
//        return ApiResponse.success(memberService.updatePassword(UUID.fromString(user.getUsername()), currentPassword, newPassword));
//    }

    @PutMapping("/check-password")
    public ApiResponse checkPassword(@AuthenticationPrincipal User user, @RequestParam String currentPassword) {
        return ApiResponse.success(memberService.checkCurrentPassword(UUID.fromString(user.getUsername()), currentPassword));
    }


    @PutMapping("/update-password")
    public ApiResponse updatePassword(@AuthenticationPrincipal User user, @RequestParam String newPassword) {
        return ApiResponse.success(memberService.updatePassword(UUID.fromString(user.getUsername()), newPassword));
    }

}