package com.example.rentitbackend.dto.sign_in.response;

import com.example.rentitbackend.common.MemberType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record SignInResponse(
        @Schema(description = "회원 UUID", example = "")
        UUID id,
        @Schema(description = "회원 닉네임", example = "노란오리")
        String nickname,
        @Schema(description = "회원 이메일", example = "duck@naver.com")
        String email,
        @Schema(description = "회원 지역", example = "시흥시")
        String location,
        @Schema(description = "회원 유형", example = "USER")
        MemberType type,

        String token
) {
}
