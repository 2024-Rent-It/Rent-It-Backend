package com.example.rentitbackend.dto.product.response;

import com.example.rentitbackend.common.DealStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ProductListByBuyerResponse(
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
        @Schema(description = "설명", example = "한 달 써보고 새로 사보시는건 어떠신지~~ 상태 좋아요")
        String description,
        @Schema(description = "이미지", example = "1bfc370f-ab3c-42ec-bdb7-8c4a02f8b0d0_IMG_9235.PNG")
        List<String> productImages,
        @Schema(description = "회원 고유키", example = "c0a00520-7abc-5b5b-8b0a-6b1c032f0e4a")
        UUID seller_id,
        @Schema(description = "판매자명", example = "하이")
        String sellerName,
        @Schema(description = "상품 등록 위치", example = "시흥시")
        String Location,
        @Schema(description = "거래 상태", example = "시흥시")
        DealStatus status,

        String buyerName,
        LocalDateTime startDate,
        LocalDateTime endDate
) {
}
