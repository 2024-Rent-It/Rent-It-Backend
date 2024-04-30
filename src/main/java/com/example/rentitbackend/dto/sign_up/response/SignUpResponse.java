package com.example.rentitbackend.dto.sign_up.response;

import com.example.rentitbackend.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record SignUpResponse(
        @Schema(description = "회원 고유키", example = "c0a00520-7abc-5b5b-8b0a-6b1c032f0e4a")
        UUID id,
        @Schema(description = "회원 아이디", example = "duck")
        String account,

        @Schema(description = "회원 닉네임", example = "노란오리")
        String nickname,

        @Schema(description = "회원 이메일", example = "duck@naver.com")
        String email,

        @Schema(description = "회원 지역", example = "시흥시")
        String location

) {
    public static SignUpResponse from(Member member) {
        return new SignUpResponse(
                member.getId(),
                member.getAccount(),
                member.getNickname(),
                member.getEmail(),
                member.getLocation()
        );
    }
}
