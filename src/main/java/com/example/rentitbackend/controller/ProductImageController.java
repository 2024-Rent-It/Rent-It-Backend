package com.example.rentitbackend.controller;

import com.example.rentitbackend.service.ProductImageService;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductImageController {
    private final ProductImageService productImageService;

    public ProductImageController(ProductImageService productImageService) {
        this.productImageService = productImageService;
    }

    @GetMapping("/images/{fileName}")
    public ResponseEntity<Resource> getImage(@PathVariable String fileName) {
        Resource resource = productImageService.loadImageAsResource(fileName);

        // 상품 이미지 파일 이름에서 확장자 추출(ex.jpg, png, heif)
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);

        // 추출한 확장자에 따라 적절한 미디어 타입 설정
        MediaType mediaType;
        switch (fileExtension.toLowerCase()) {
            case "jpeg":
            case "jpg":
                mediaType = MediaType.IMAGE_JPEG;
                break;
            case "png":
                mediaType = MediaType.IMAGE_PNG;
                break;
            case "gif":
                mediaType = MediaType.IMAGE_GIF;
                break;
            case "heif":
                mediaType = MediaType.valueOf("image/heif");
                break;
            default:
                mediaType = MediaType.APPLICATION_OCTET_STREAM;
                break;
        }

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(resource);
    }
}
