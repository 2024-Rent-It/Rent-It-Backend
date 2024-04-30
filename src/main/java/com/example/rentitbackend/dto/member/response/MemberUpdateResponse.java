package com.example.rentitbackend.dto.member.response;

import com.example.rentitbackend.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;

public record MemberUpdateResponse(
        @Schema(description = "회원 정보 수정 성공 여부", example = "true")
        boolean result,
        @Schema(description = "회원 닉네임", example = "노란오리")
        String nickname,
        @Schema(description = "회원 이메일", example = "duck@naver.com")
        String email,
        @Schema(description = "회원 지역", example = "시흥시")
        String location
) {
    public static MemberUpdateResponse of(boolean result, Member member) {
        return new MemberUpdateResponse(result, member.getNickname(), member.getEmail(), member.getLocation());
    }
}
