package com.example.rentitbackend.service;

import com.example.rentitbackend.dto.product.request.ProductRegisterRequest;
import com.example.rentitbackend.entity.Member;
import com.example.rentitbackend.entity.Notice;
import com.example.rentitbackend.entity.Product;
import com.example.rentitbackend.entity.ProductImage;
import com.example.rentitbackend.repository.MemberRepository;
import com.example.rentitbackend.repository.ProductImageRepository;
import com.example.rentitbackend.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Autowired
    public ProductService(ProductRepository productRepository, ProductImageRepository productImageRepository, MemberRepository memberRepository) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
        this.memberRepository = memberRepository;
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

    @Transactional
    public Product updateProduct(Long productId, ProductRegisterRequest request) {
        // 업데이트할 상품을 찾음
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID에 해당하는 상품을 찾을 수 없습니다: " + productId));

        product.setTitle(request.title());
        product.setCategory(request.category());
        product.setDuration(request.duration());
        product.setPrice(request.price());
        product.setDescription(request.description());

        List<ProductImage> existingImages = product.getImages();
        for (ProductImage existingImage : existingImages) {
            productImageRepository.delete(existingImage);
        }

        for (MultipartFile file : request.images()) {
            ProductImage productImage = new ProductImage();
            productImage.setUploadFileName(file.getOriginalFilename());
            productImage.setStoreFileName(productImageService.saveImage(file));
            productImage.setProduct(product);
            productImageRepository.save(productImage);
            product.getImages().add(productImage);
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
            // Product 엔터티 삭제
            productRepository.delete(product);
        } else {
            // 지정된 ID에 해당하는 Product가 없을 경우 처리할 내용 추가
            throw new IllegalArgumentException("Product not found with ID: " + productId);
        }
    }
}
