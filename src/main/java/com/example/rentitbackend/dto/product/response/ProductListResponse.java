package com.example.rentitbackend.dto.product.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

public record ProductListResponse(
        @Schema(description = "상품아이디", example = "1")
        Long id,
        @Schema(description = "상품명", example = "아이폰14")
        String title,
        @Schema(description = "카테고리", example = "디지털기기")
        String category,
        @Schema(description = "최대 기간", example = "1주일")
        String duration,
        @Schema(description = "가격", example = "10000000")
        int price,
        @Schema(description = "이미지", example = "1bfc370f-ab3c-42ec-bdb7-8c4a02f8b0d0_IMG_9235.PNG")
        List<String> productImages

) {
}
