package com.example.rentitbackend.dto.product.request;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record RentProductRequest (
        String buyerNickname,

        LocalDateTime startDate,

        LocalDateTime endDate
){

}
