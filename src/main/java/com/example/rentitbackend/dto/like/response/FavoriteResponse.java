package com.example.rentitbackend.dto.like.response;


import com.example.rentitbackend.dto.product.response.ProductListResponse;

public record FavoriteResponse(
        Long id,
        ProductListResponse product
) {
}
