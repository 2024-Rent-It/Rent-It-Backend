package com.example.rentitbackend.dto.product.request;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record ProductRegisterRequest(
        @Schema(description = "상품명", example = "아이폰14")
        String title,
        @Schema(description = "카테고리", example = "디지털기기")
        String category,
        @Schema(description = "최대 기간", example = "1주일")
        String duration,
        @Schema(description = "가격", example = "10000000")
        int price,
        @Schema(description = "설명", example = "한 달 써보고 새로 사보시는건 어떠신지~~ 상태 좋아요")
        String description,
        @Schema(description = "상품 이미지")
        List<MultipartFile> images
    ) {
}
