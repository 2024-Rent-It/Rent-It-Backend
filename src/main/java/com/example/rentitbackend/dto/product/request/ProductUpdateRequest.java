package com.example.rentitbackend.dto.product.request;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record ProductUpdateRequest(
        String title,
        String category,
        String duration,
        int price,
        String description,
        List<MultipartFile> images,
        List<Long> deletedImages
) {}

