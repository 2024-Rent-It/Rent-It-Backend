package com.example.rentitbackend.controller;

import com.example.rentitbackend.dto.product.request.ProductRegisterRequest;
import com.example.rentitbackend.dto.product.request.ProductUpdateRequest;
import com.example.rentitbackend.dto.product.request.RentProductRequest;
import com.example.rentitbackend.dto.product.response.ProductDetailResponse;
import com.example.rentitbackend.dto.product.response.ProductListByBuyerResponse;
import com.example.rentitbackend.dto.product.response.ProductListBySellerResponse;
import com.example.rentitbackend.dto.product.response.ProductListResponse;
import com.example.rentitbackend.entity.Member;
import com.example.rentitbackend.entity.Product;
import com.example.rentitbackend.entity.ProductImage;
//import com.example.rentitbackend.file.FileStore;
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
    public ProductController(ProductService productService, MemberService memberService) {
        this.productService = productService;
        this.memberService = memberService;
    }

    private ResponseEntity<List<ProductListResponse>> getListResponseEntity(List<Product> productList) {
        List<ProductListResponse> productResponseList = productList.stream().map(product -> {
            ProductListResponse response = new ProductListResponse(
                    product.getId(),
                    product.getTitle(),
                    product.getCategory(),
                    product.getDuration(),
                    product.getPrice(),
                    product.getImages().stream().map(ProductImage::getStoreFileName).collect(Collectors.toList())
            );
            return response;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(productResponseList);
    }

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @UserAuthorize
    public ResponseEntity<Product> registerProduct(@AuthenticationPrincipal User user, @ModelAttribute ProductRegisterRequest request) {
        Product product = productService.registerProduct(UUID.fromString(user.getUsername()), request);
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }

    //    @PutMapping(value ="/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<Product> updateProduct(@AuthenticationPrincipal User user, @PathVariable Long productId,
//                                                 @ModelAttribute ProductUpdateRequest request) {
//        Product updatedProduct = productService.updateProduct(productId, request);
//        return ResponseEntity.ok(updatedProduct);
//    }
    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long productId,
            @ModelAttribute ProductUpdateRequest request) {
        Product updatedProduct = productService.updateProduct(productId, request);
        return ResponseEntity.ok(updatedProduct);
    }


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
    public ResponseEntity<List<ProductListResponse>> getProductsByLocation(@PathVariable String location) {
        List<Product> productList = productService.getProductsByLocation(location);
        return getListResponseEntity(productList);
    }

    @Operation(summary = "지역 내 상품 검색 조회(최신 등록 순)")
    @GetMapping("/search")
    public ResponseEntity<List<ProductListResponse>> searchProductsByTitleAndLocation(@RequestParam String searchKeyword,
                                                                                      @RequestParam String location) {
        List<Product> productList = productService.searchProductsByTitleAndLocation(searchKeyword, location);
        return getListResponseEntity(productList);
    }

    @Operation(summary = "지역 내 상품 고가순 검색 조회")
    @GetMapping("/search/high-price")
    public ResponseEntity<List<ProductListResponse>> searchProductsByTitleAndLocationOrderByPriceHighToLow(
            @RequestParam String searchKeyword,
            @RequestParam String location
    ) {
        List<Product> productList = productService.searchProductsByTitleAndLocationOrderByPriceHighToLow(searchKeyword, location);
        return getListResponseEntity(productList);
    }

    @Operation(summary = "지역 내 상품 저가순 검색 조회")
    @GetMapping("/search/low-price")
    public ResponseEntity<List<ProductListResponse>> searchProductsByTitleAndLocationOrderByPriceLowToHigh(
            @RequestParam String searchKeyword,
            @RequestParam String location
    ) {
        List<Product> productList = productService.searchProductsByTitleAndLocationOrderByPriceLowToHigh(searchKeyword, location);
        return getListResponseEntity(productList);
    }

    @Operation(summary = "한 지역 내 카테고리 별 상품 목록 조회")
    @GetMapping("/location-category/{location}/{category}")
    public ResponseEntity<List<ProductListResponse>> getProductsByLocationAndCategory(@PathVariable String location, @PathVariable String category) {
        List<Product> productList = productService.getProductsByLocationAndCategory(location, category);
        return getListResponseEntity(productList);
    }


    @Operation(summary = "판매자의 상품 목록 조회 (최신순, 지역별)")
    @GetMapping("/seller")
    public ResponseEntity<List<ProductListResponse>> getProductsBySellerNicknameAndLocationOrderByCreatedAtDesc(
            @RequestParam String sellerNickname,
            @RequestParam String location
    ) {
        List<Product> productList = productService.getProductsBySellerNicknameAndLocationOrderByCreatedAtDesc(sellerNickname, location);
        return getListResponseEntity(productList);
    }

    //    @Operation(summary = "판매자 상품 목록 조회")
//    @GetMapping("/seller/{nickname}")
//    public ResponseEntity<List<ProductListBySellerResponse>> getProductsBySeller(@PathVariable String nickname) {
//        Member seller = memberService.findByNickname(nickname);
//        List<Product> products = productService.getProductsBySeller(seller);
//        List<ProductListBySellerResponse> productResponseList = products.stream().map(product -> {
//            ProductListBySellerResponse response = new ProductListBySellerResponse(
//                    product.getTitle(),
//                    product.getCategory(),
//                    product.getDuration(),
//                    product.getPrice(),
//                    product.getDescription(),
//                    product.getImages().stream().map(ProductImage::getStoreFileName).collect(Collectors.toList()),
//                    product.getSeller().getId(),
//                    product.getSeller().getNickname(),
//                    product.getLocation(),
//                    product.getStatus()
//            );
//            return response;
//        }).collect(Collectors.toList());
//        return ResponseEntity.ok(productResponseList);
//    }
    @Operation(summary = "판매자 상품 목록 조회(전체)")
    @GetMapping("/seller/{nickname}")
    public ResponseEntity<List<ProductListBySellerResponse>> getProductsBySeller(@PathVariable String nickname) {
        Member seller = memberService.findByNickname(nickname);
        List<Product> products = productService.getProductsBySeller(seller);
        List<ProductListBySellerResponse> productResponseList = products.stream().map(product -> {
            ProductListBySellerResponse response = new ProductListBySellerResponse(
                    product.getId(),
                    product.getTitle(),
                    product.getCategory(),
                    product.getDuration(),
                    product.getPrice(),
                    product.getDescription(),
                    product.getImages().stream().map(ProductImage::getStoreFileName).collect(Collectors.toList()),
                    product.getSeller().getId(),
                    product.getSeller().getNickname(),
                    product.getLocation(),
                    product.getStatus(),
                    product.getBuyer() != null ? product.getBuyer().getNickname() : "", // 구매자가 null인 경우 처리
                    product.getStartDate() != null ? product.getStartDate() : null, // 시작일자가 null인 경우 처리
                    product.getEndDate() != null ? product.getEndDate() : null // 종료일자가 null인 경우 처리
            );
            return response;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(productResponseList);
    }

    @Operation(summary = "구매자 상품 목록 조회")
    @GetMapping("/buyer/{nickname}")
    public ResponseEntity<List<ProductListByBuyerResponse>> getProductsByBuyer(@PathVariable String nickname) {
        Member buyer = memberService.findByNickname(nickname);
        List<Product> products = productService.getProductsByBuyer(buyer);
        List<ProductListByBuyerResponse> productResponseList = products.stream().map(product -> {
            ProductListByBuyerResponse response = new ProductListByBuyerResponse(
                    product.getId(),
                    product.getTitle(),
                    product.getCategory(),
                    product.getDuration(),
                    product.getPrice(),
                    product.getDescription(),
                    product.getImages().stream().map(image -> image.getStoreFileName()).collect(Collectors.toList()),
                    UUID.fromString(product.getSeller().getId().toString()),
                    product.getSeller().getNickname(),
                    product.getLocation(),
                    product.getStatus(),
                    product.getBuyer() != null ? product.getBuyer().getNickname() : "", // 구매자가 null인 경우 처리
                    product.getStartDate() != null ? product.getStartDate() : null, // 시작일자가 null인 경우 처리
                    product.getEndDate() != null ? product.getEndDate() : null // 종료일자가 null인 경우 처리
            );
            return response;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(productResponseList);
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
    }


    @Operation(summary = "상품 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProductWithImages(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "렌트 가능 -> 렌트 중으로 상태 변경")
    @PostMapping("/{productId}/rent")
    public ResponseEntity<String> rentProduct(@PathVariable Long productId, @RequestBody RentProductRequest request) {
        try {
            productService.rentProduct(productId, request);
            return ResponseEntity.ok("상품이 성공적으로 렌트되었습니다.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "렌트 완료로 상태 변경")
    @PutMapping("/{productId}/complete-rent")
    public ResponseEntity<String> completeRent(@PathVariable Long productId) {
        try {
            productService.completeRent(productId);
            return ResponseEntity.ok("상품이 성공적으로 렌트 완료 처리되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "렌트 가능으로 상태 변경")
    @PutMapping("/{productId}/rent-available")
    public ResponseEntity<String> rentAvailable(@PathVariable Long productId) {
        try {
            productService.rentAvailable(productId);
            return ResponseEntity.ok("상품이 성공적으로 렌트 가능 처리되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @PostMapping("/update-all-status-available")
    public ResponseEntity<String> updateAllProductStatusToRentAvailable() {
        productService.updateAllProductStatusToRentAvailable();
        return ResponseEntity.ok("모든 상품의 상태를 '렌트가능'으로 변경하였습니다.");
    }
}

