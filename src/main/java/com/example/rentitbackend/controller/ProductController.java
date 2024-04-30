package com.example.rentitbackend.controller;

import com.example.rentitbackend.dto.product.request.ProductRegisterRequest;
import com.example.rentitbackend.dto.product.response.ProductDetailResponse;
import com.example.rentitbackend.dto.product.response.ProductListBySellerResponse;
import com.example.rentitbackend.entity.Member;
import com.example.rentitbackend.entity.Product;
import com.example.rentitbackend.entity.ProductImage;
import com.example.rentitbackend.file.FileStore;
import com.example.rentitbackend.security.UserAuthorize;
import com.example.rentitbackend.service.MemberService;
import com.example.rentitbackend.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;
    private final MemberService memberService;

    @Autowired
    public ProductController(FileStore fileStore, ProductService productService, MemberService memberService) {
        this.productService = productService;
        this.memberService = memberService;
    }

    //    @PostMapping(value="/register", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @UserAuthorize
    public ResponseEntity<Product> registerProduct(@AuthenticationPrincipal User user, @ModelAttribute ProductRegisterRequest request) {
        Product product = productService.registerProduct(UUID.fromString(user.getUsername()), request);
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }


//    @Operation(summary = "상품 목록 조회")
//    @GetMapping
//    public ResponseEntity<List<Product>> getAllProducts() {
//        List<Product> products = productService.getAllProducts();
//        return ResponseEntity.ok(products);
//    }

//    @Operation(summary = "상품 목록 조회")
//    @GetMapping
//    public ResponseEntity<Page<ProductDetailResponse>> getAllProducts(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "9") int size
//    ) {
//        Pageable pageable = PageRequest.of(page, size);
//        Page<Product> productPage = productService.getAllProducts(pageable);
//        Page<ProductDetailResponse> productResponsePage = productPage.map(product -> {
//            ProductDetailResponse response = new ProductDetailResponse(
//                    product.getTitle(),
//                    product.getCategory(),
//                    product.getDuration(),
//                    product.getPrice(),
//                    product.getDescription(),
//                    product.getImages().stream().map(ProductImage::getStoreFileName).collect(Collectors.toList()),
//                    product.getSeller().getId(),
//                    product.getSeller().getNickname(),
//                    product.getLocation()
//            );
//            return response;
//        });
//        return ResponseEntity.ok(productResponsePage);
//    }

    @GetMapping
    public ResponseEntity<List<ProductDetailResponse>> getAllProducts() {
        List<Product> productList = productService.getAllProducts(); // 모든 상품을 가져옵니다.
        List<ProductDetailResponse> productResponseList = productList.stream().map(product -> {
            ProductDetailResponse response = new ProductDetailResponse(
                    product.getTitle(),
                    product.getCategory(),
                    product.getDuration(),
                    product.getPrice(),
                    product.getDescription(),
                    product.getImages().stream().map(ProductImage::getStoreFileName).collect(Collectors.toList()),
                    product.getSeller().getId(),
                    product.getSeller().getNickname(),
                    product.getLocation()
            );
            return response;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(productResponseList);
    }
    @Operation(summary = "지역별 상품 목록 조회")
    @GetMapping("/location/{location}")
    public ResponseEntity<List<Product>> getProductsByLocation(@PathVariable String location) {
        List<Product> products = productService.getProductsByLocation(location);
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "판매자 상품 목록 조회")
    @GetMapping("/seller/{nickname}")
    public ResponseEntity<List<ProductListBySellerResponse>> getProductsBySeller(@PathVariable String nickname) {
        Member seller = memberService.findByNickname(nickname);
        List<Product> products = productService.getProductsBySeller(seller);
        List<ProductListBySellerResponse> productResponseList = products.stream().map(product -> {
            ProductListBySellerResponse response = new ProductListBySellerResponse(
                    product.getTitle(),
                    product.getCategory(),
                    product.getDuration(),
                    product.getPrice(),
                    product.getDescription(),
                    product.getImages().stream().map(ProductImage::getStoreFileName).collect(Collectors.toList()),
                    product.getSeller().getId(),
                    product.getSeller().getNickname(),
                    product.getLocation(),
                    product.getStatus()
            );
            return response;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(productResponseList);
//        return ResponseEntity.ok(products);
    }

    @Operation(summary = "상품 상세 조회")
    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailResponse> getProductById(@PathVariable Long id) {
        Optional<Product> productOptional = productService.getProductById(id);
        return productOptional.map(product -> {
            ProductDetailResponse response = new ProductDetailResponse(
                    product.getTitle(),
                    product.getCategory(),
                    product.getDuration(),
                    product.getPrice(),
                    product.getDescription(),
                    product.getImages().stream().map(ProductImage::getStoreFileName).collect(Collectors.toList()),
                    product.getSeller().getId(),
                    product.getSeller().getNickname(),
                    product.getLocation()
            );
            return ResponseEntity.ok(response);
        }).orElse(ResponseEntity.notFound().build());
        //        Optional<Product> productOptional = productService.getProductById(id);
//        return productOptional.map(product -> {
//            List<String> imagePaths = product.getImages().stream()
//                    .map(image -> Path.of(uploadDir, image.getStoreFileName()).toString())
//                    .collect(Collectors.toList());
//
//            ProductDetailResponse response = new ProductDetailResponse(
//                    product.getTitle(),
//                    product.getCategory(),
//                    product.getDuration(),
//                    product.getPrice(),
//                    product.getDescription(),
//                    imagePaths
//            );
//            return ResponseEntity.ok(response);
//        }).orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "상품 검색 조회")
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProductsByKeyword(@RequestParam String keyword) {
        List<Product> products = productService.searchProductsByKeyword(keyword);
        return ResponseEntity.ok(products);
    }

//    @DeleteMapping("/{id}")
//    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
//        try {
//            productService.deleteProductWithImages(id);
//            return ResponseEntity.ok().body("Product with ID " + id + " and its images have been deleted successfully.");
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.notFound().body(e.getMessage());
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the product: " + e.getMessage());
//        }
//    }

    @Operation(summary = "상품 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProductWithImages(id);
        return ResponseEntity.noContent().build();
    }
}

