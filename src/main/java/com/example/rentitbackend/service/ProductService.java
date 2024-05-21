package com.example.rentitbackend.service;

import com.example.rentitbackend.common.DealStatus;
import com.example.rentitbackend.dto.product.request.ProductRegisterRequest;
import com.example.rentitbackend.dto.product.request.ProductUpdateRequest;
import com.example.rentitbackend.dto.product.request.RentProductRequest;
import com.example.rentitbackend.entity.*;
import com.example.rentitbackend.repository.FavoriteRepository;
import com.example.rentitbackend.repository.MemberRepository;
import com.example.rentitbackend.repository.ProductImageRepository;
import com.example.rentitbackend.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final MemberRepository memberRepository;
    private final FavoriteRepository favoriteRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, ProductImageRepository productImageRepository, MemberRepository memberRepository, FavoriteRepository favoriteRepository) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
        this.memberRepository = memberRepository;
        this.favoriteRepository = favoriteRepository;
    }

    @Autowired
    private ProductImageService productImageService;

    @Transactional
    public Product registerProduct(UUID sellerId, ProductRegisterRequest request) {
        // sellerId로 Member를 찾음
        Member seller = memberRepository.findById(sellerId)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID에 해당하는 회원을 찾을 수 없습니다: " + sellerId));

        Product product = new Product();
        product.setTitle(request.title());
        product.setCategory(request.category());
        product.setDuration(request.duration());
        product.setPrice(request.price());
        product.setDescription(request.description());
        product.setLocation(seller.getLocation());
        product.setSeller(seller);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        // 상품 이미지 저장
        for (MultipartFile file : request.images()) {
            ProductImage productImage = new ProductImage();

            productImage.setUploadFileName(file.getOriginalFilename());
            productImage.setStoreFileName(productImageService.saveImage(file));
            productImage.setProduct(product);
            productImageRepository.save(productImage);

            product.getImages().add(productImage);
        }
        return productRepository.save(product);
    }



    @Transactional
    public List<Product> getAllProducts() {
        return productRepository.findAllOrderedByCreatedAtDesc();
    }
//    @Transactional
//    public Page<Product> getAllProducts(Pageable pageable) {
//        return productRepository.findAll(pageable);
//    }
    @Transactional
    public List<Product> getProductsByLocation(String location) {

//        return productRepository.findByLocation(location);
        return productRepository.findAllByLocationOrderByCreatedAtDesc(location);
    }

    @Transactional
    public List<Product> getProductsByLocationAndCategory(String location, String category) {
        return productRepository.findAllByLocationAndCategoryOrderByCreatedAtDesc(location, category);
    }

    @Transactional
    public List<Product> getProductsBySeller(Member seller) {
        return productRepository.findBySeller(seller);
    }

    @Transactional
    public List<Product> getProductsByBuyer(Member buyer) {
        return productRepository.findByBuyer(buyer);
    }

    @Transactional
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

//    @Transactional
//    public List<Product> searchProductsByKeyword(String keyword) {
//        return productRepository.findByTitleContaining(keyword);
//    }

    @Transactional
    public List<Product> searchProductsByTitleAndLocation(String searchKeyword, String location) {
        return productRepository.findAllByTitleContainingAndLocationOrderByCreatedAtDesc(searchKeyword, location);
    }

    @Transactional
    public List<Product> searchProductsByTitleAndLocationOrderByPriceHighToLow(String searchKeyword, String location) {
        return productRepository.findAllByTitleContainingAndLocationOrderByPriceDesc(searchKeyword, location);
    }

    @Transactional
    public List<Product> searchProductsByTitleAndLocationOrderByPriceLowToHigh(String searchKeyword, String location) {
        return productRepository.findAllByTitleContainingAndLocationOrderByPriceAsc(searchKeyword, location);
    }

    @Transactional
    public List<Product> getProductsBySellerNicknameAndLocationOrderByCreatedAtDesc(String sellerNickname, String location) {
        Member seller = memberRepository.findByNickname(sellerNickname)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID에 해당하는 회원을 찾을 수 없습니다: " + sellerNickname));
        return productRepository.findAllBySellerAndLocationOrderByCreatedAtDesc(seller, location);
    }

//    @Transactional
//    public Product updateProduct(Long productId, ProductRegisterRequest request) {
//        // 업데이트할 상품을 찾음
//        Product product = productRepository.findById(productId)
//                .orElseThrow(() -> new EntityNotFoundException("해당 ID에 해당하는 상품을 찾을 수 없습니다: " + productId));
//
//        product.setTitle(request.title());
//        product.setCategory(request.category());
//        product.setDuration(request.duration());
//        product.setPrice(request.price());
//        product.setDescription(request.description());
//
//        List<ProductImage> existingImages = product.getImages();
//        for (ProductImage existingImage : existingImages) {
//            productImageRepository.delete(existingImage);
//        }
//
//        for (MultipartFile file : request.images()) {
//            ProductImage productImage = new ProductImage();
//            productImage.setUploadFileName(file.getOriginalFilename());
//            productImage.setStoreFileName(productImageService.saveImage(file));
//            productImage.setProduct(product);
//            productImageRepository.save(productImage);
//            product.getImages().add(productImage);
//        }
//        product.setUpdatedAt(LocalDateTime.now());
//
//        return productRepository.save(product);
//    }
@Transactional
public Product updateProduct(Long productId, ProductUpdateRequest request) {
    // 업데이트할 상품을 찾음
    Product product = productRepository.findById(productId)
            .orElseThrow(() -> new EntityNotFoundException("해당 ID에 해당하는 상품을 찾을 수 없습니다: " + productId));

    product.setTitle(request.title());
    product.setCategory(request.category());
    product.setDuration(request.duration());
    product.setPrice(request.price());
    product.setDescription(request.description());

    // 삭제할 이미지 처리
    if (request.deletedImages() != null && !request.deletedImages().isEmpty()) {
        for (Long imageId : request.deletedImages()) {
            ProductImage productImage = productImageRepository.findById(imageId)
                    .orElseThrow(() -> new EntityNotFoundException("해당 ID에 해당하는 이미지를 찾을 수 없습니다: " + imageId));
            productImageRepository.delete(productImage);
        }
    }

    // 새로운 이미지 추가
    if (request.images() != null && !request.images().isEmpty()) {
        for (MultipartFile file : request.images()) {
            ProductImage productImage = new ProductImage();
            productImage.setUploadFileName(file.getOriginalFilename());
            productImage.setStoreFileName(productImageService.saveImage(file));
            productImage.setProduct(product);
            productImageRepository.save(productImage);
            product.getImages().add(productImage);
        }
    }

    product.setUpdatedAt(LocalDateTime.now());
    return productRepository.save(product);
}

    @Transactional
    public void deleteProductWithImages(Long productId) {
        // Product 엔터티 조회
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            // Product에 연결된 모든 ProductImage 엔터티 조회
            List<ProductImage> productImages = product.getImages();
            // 연결된 모든 ProductImage 엔터티 삭제
            productImageRepository.deleteAll(productImages);

            List<Favorite> favorites = favoriteRepository.findByProduct(product);
            // 연결된 모든 Favorite 엔터티 삭제
            favoriteRepository.deleteAll(favorites);

            // Product 엔터티 삭제
            productRepository.delete(product);
        } else {
            // 지정된 ID에 해당하는 Product가 없을 경우 처리할 내용 추가
            throw new IllegalArgumentException("Product not found with ID: " + productId);
        }
    }

    public void rentProduct(Long productId, RentProductRequest request) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            if (product.getStatus().equals(DealStatus.렌트가능)) {
                // 해당 상품이 렌트 가능한 상태라면 진행
                Optional<Member> optionalBuyer = memberRepository.findByNickname(request.buyerNickname());
                if (optionalBuyer.isPresent()) {
                    Member buyer = optionalBuyer.get();
                    product.setStatus(DealStatus.렌트중);
                    product.setBuyer(buyer);
                    product.setStartDate(request.startDate());
                    product.setEndDate(request.endDate());
                    productRepository.save(product);
                } else {
                    throw new IllegalArgumentException("해당 nickname을 가진 구매자를 찾을 수 없습니다.");
                }
            } else {
                throw new IllegalStateException("해당 상품은 현재 렌트가능한 상태가 아닙니다.");
            }
        } else {
            throw new IllegalArgumentException("해당 ID를 가진 상품을 찾을 수 없습니다.");
        }
    }

    public void completeRent(Long productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            if (product.getStatus().equals(DealStatus.렌트가능) || product.getStatus().equals(DealStatus.렌트중)) {
                // 해당 상품이 렌트 가능이나 렌트 중인 상태일 때만 렌트 완료 처리 가능
                product.setStatus(DealStatus.렌트완료);
                productRepository.save(product);
            } else {
                throw new IllegalArgumentException("해당 상품은 현재 렌트 중인 상태가 아닙니다.");
            }
        } else {
            throw new IllegalArgumentException("해당 ID를 가진 상품을 찾을 수 없습니다.");
        }
    }

    public void rentAvailable(Long productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            if (product.getStatus().equals(DealStatus.렌트완료) || product.getStatus().equals(DealStatus.렌트중)) {
                // 해당 상품이 렌트 가능이나 렌트 중인 상태일 때만 렌트 완료 처리 가능
                product.setStatus(DealStatus.렌트가능);
                product.setBuyer(null); // buyer 초기화
                product.setStartDate(null); // startDate 초기화
                product.setEndDate(null); // endDate 초기화
                productRepository.save(product);
            } else {
                throw new IllegalArgumentException("해당 상품은 현재 렌트 중인 상태가 아닙니다.");
            }
        } else {
            throw new IllegalArgumentException("해당 ID를 가진 상품을 찾을 수 없습니다.");
        }
    }

    @Transactional
    public void updateAllProductStatusToRentAvailable() {
        productRepository.findAll().forEach(product -> product.setStatus(DealStatus.렌트가능));
    }
}
