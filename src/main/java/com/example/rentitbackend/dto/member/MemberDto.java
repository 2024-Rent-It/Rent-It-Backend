package com.example.rentitbackend.dto.member;

import com.example.rentitbackend.common.MemberType;
import com.example.rentitbackend.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

public class MemberDto {
    @Getter
    @Builder
    @AllArgsConstructor
    public static class Response {
//        @Schema(description = "회원 고유키", example = "c0a00520-7abc-5b5b-8b0a-6b1c032f0e4a")
//        UUID id,
//        @Schema(description = "회원 아이디", example = "duck")
//        String account,
//        @Schema(description = "회원 닉네임", example = "노란오리")
//        String nickname,
//        @Schema(description = "회원 이메일", example = "duck@naver.com")
//        String email,
//        @Schema(description = "회원 지역", example = "시흥시")
//        String location,
//        @Schema(description = "회원 타입", example = "USER")
//        MemberType type,
//        @Schema(description = "회원 생성일", example = "2024-03-27T15:00:00")
//        LocalDateTime createdAt
        private UUID id;
        private String account;
        private String nickname;
        private String email;
        private String location;
        private MemberType type;
        private LocalDateTime createdAt;


        public static Response of(Member member) {
            return Response.builder()
                    .id(member.getId())
                    .account(member.getAccount())
                    .nickname(member.getNickname())
                    .email(member.getEmail())
                    .location(member.getLocation())
                    .type(member.getType())
                    .createdAt(member.getCreatedAt())
                    .build();
        }
    }


}