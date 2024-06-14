package com.example.rentitbackend.service;

import com.example.rentitbackend.common.DealStatus;
import com.example.rentitbackend.dto.product.request.ProductRegisterRequest;
import com.example.rentitbackend.dto.product.request.ProductUpdateRequest;
import com.example.rentitbackend.dto.product.request.RentProductRequest;
import com.example.rentitbackend.entity.*;
import com.example.rentitbackend.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final MemberRepository memberRepository;
    private final FavoriteRepository favoriteRepository;
    private final KeywordRepository keywordRepository;
    private final KeywordNotificationRepository keywordNotificationRepository;
    private final PriceDropNotificationRepository priceDropNotificationRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, ProductImageRepository productImageRepository, MemberRepository memberRepository, FavoriteRepository favoriteRepository, KeywordRepository keywordRepository, KeywordNotificationRepository keywordNotificationRepository, PriceDropNotificationRepository priceDropNotificationRepository) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
        this.memberRepository = memberRepository;
        this.favoriteRepository = favoriteRepository;
        this.keywordRepository = keywordRepository;
        this.keywordNotificationRepository = keywordNotificationRepository;
        this.priceDropNotificationRepository = priceDropNotificationRepository;
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

        // 키워드 기반 푸시 알림 전송
        sendKeywordPushNotifications(product);

        return productRepository.save(product);
    }

    private void sendKeywordPushNotifications(Product product) {
        List<Keyword> matchingKeywords = keywordRepository.findAll()
                .stream()
                .filter(keyword -> product.getTitle().contains(keyword.getName()))
                .collect(Collectors.toList());
        List<String> tokens = matchingKeywords.stream()
                .flatMap(keyword -> keyword.getMember().getTokens().stream())
                .map(Token::getToken)
                .collect(Collectors.toList());

        if (!tokens.isEmpty()) {
            sendPushNotification(tokens, product.getTitle());
        }

        // 알림 정보 저장
        for (Keyword keyword : matchingKeywords) {
            KeywordNotification notification = new KeywordNotification();
            notification.setMember(keyword.getMember());
            notification.setType("키워드 알람");
            notification.setMessage(String.format("\"%s\" 상품이 등록되었습니다! 확인하러 가 볼까요?", keyword.getName()));
            notification.setProductId(product.getId());
            notification.setKeyword(keyword.getName());
            notification.setCreatedAt(LocalDateTime.now());
            keywordNotificationRepository.save(notification);
        }
    }

    private void sendPushNotification(List<String> tokens, String productName) {
        String url = "https://exp.host/--/api/v2/push/send";
        RestTemplate restTemplate = new RestTemplate();

        List<PushMessage> messages = tokens.stream()
                .map(token -> new PushMessage(token, productName))
                .collect(Collectors.toList());

        restTemplate.postForEntity(url, messages, String.class);
    }

    private static class PushMessage {
        private final String to;
        private final String sound = "default";
        private final String title = "키워드 알림 도착!";
        private final String body;

        public PushMessage(String to, String productName) {
            this.to = to;
            this.body = String.format("알림 등록한 상품 '%s' 신규 등록!", productName.isEmpty() ? "기본 상품명" : productName);
        }

        public String getTo() {
            return to;
        }

        public String getSound() {
            return sound;
        }

        public String getTitle() {
            return title;
        }

        public String getBody() {
            return body;
        }
    }


    @Transactional
    public List<Product> getAllProducts() {
        return productRepository.findAllOrderedByCreatedAtDesc();
    }

    @Transactional
    public List<Product> getProductsByLocation(String location) {

//        return productRepository.findByLocation(location);
        return productRepository.findAllByLocationOrderByCreatedAtDesc(location);
    }

    public List<Product> getProductsByLocationAndStatus(String location, DealStatus status) {
        return productRepository.findByLocationAndStatusOrderByCreatedAtDesc(location, status);
    }


    @Transactional
    public List<Product> getProductsByLocationAndCategory(String location, String category) {
        return productRepository.findAllByLocationAndCategoryOrderByCreatedAtDesc(location, category);
    }

    @Transactional
    public List<Product> getProductsBySeller(Member seller) {

//        return productRepository.findBySeller(seller);
        return productRepository.findBySellerOrderByCreatedAtDesc(seller);
    }

    @Transactional
    public List<Product> getProductsByBuyer(Member buyer) {

//        return productRepository.findByBuyer(buyer);
        return productRepository.findByBuyerOrderByCreatedAtDesc(buyer);
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
    public Product updateProduct(Long productId, ProductUpdateRequest request) {
        // 업데이트할 상품을 찾음
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID에 해당하는 상품을 찾을 수 없습니다: " + productId));

        // 이전 가격 저장
        int previousPrice = product.getPrice();

        // 상품의 필드 업데이트
        product.setTitle(request.title());
        product.setCategory(request.category());
        product.setDuration(request.duration());
        product.setPrice(request.price());
        product.setDescription(request.description());

        // 기존 이미지 삭제
        Iterator<ProductImage> iterator = product.getImages().iterator();
        while (iterator.hasNext()) {
            ProductImage existingImage = iterator.next();
            productImageRepository.delete(existingImage);
            iterator.remove(); // Iterator를 사용하여 삭제
        }

        // 새로운 이미지 추가
        List<MultipartFile> files = request.images();
        if (files != null && !files.isEmpty()) { // 이미지가 제공되었는지 확인
            for (MultipartFile file : files) {
                ProductImage productImage = new ProductImage();
                productImage.setUploadFileName(file.getOriginalFilename());
                productImage.setStoreFileName(productImageService.saveImage(file));
                productImage.setProduct(product);
                productImageRepository.save(productImage);
                product.getImages().add(productImage);
            }
        }

        // 가격이 떨어졌는지 확인
        if (previousPrice > request.price()) {
            sendPriceDropNotifications(product, previousPrice, request.price());
        }

        // 변경된 내용 저장
        product.setUpdatedAt(LocalDateTime.now());
        return productRepository.save(product);
    }

    private void sendPriceDropNotifications(Product product, int previousPrice, int newPrice) {
        // 상품을 찜한 회원 조회
        List<Member> interestedMembers = favoriteRepository.findByProductId(product.getId())
                .stream()
                .map(Favorite::getMember)
                .collect(Collectors.toList());

        List<String> tokens = interestedMembers.stream()
                .flatMap(member -> member.getTokens().stream())
                .map(Token::getToken)
                .collect(Collectors.toList());

        if (!tokens.isEmpty()) {
            sendPriceDropPushNotifications(tokens, product.getTitle(), previousPrice, newPrice);
        }

        // 알림 정보 저장
        for (Member member : interestedMembers) {
            PriceDropNotification notification = new PriceDropNotification();
            notification.setMember(member);
            notification.setType("가격 하락 알람");
            notification.setMessage(String.format("찜한 상품 \"%s\"의 가격이 %d원에서 %d원으로 떨어졌습니다!", product.getTitle(), previousPrice, newPrice));
            notification.setProductId(product.getId());
            notification.setCreatedAt(LocalDateTime.now());
            notification.setPriceDropAmount(previousPrice-newPrice);
            priceDropNotificationRepository.save(notification);
        }
    }
    private void sendPriceDropPushNotifications(List<String> tokens, String productName, int previousPrice, int newPrice) {
        int priceDropAmount = previousPrice - newPrice;
        String url = "https://exp.host/--/api/v2/push/send";
        RestTemplate restTemplate = new RestTemplate();

        List<PriceDropPushMessage> messages = tokens.stream()
                .map(token -> new PriceDropPushMessage(token, productName, previousPrice, newPrice, priceDropAmount))
                .collect(Collectors.toList());

        restTemplate.postForEntity(url, messages, String.class);
    }

    private static class PriceDropPushMessage {
        private final String to;
        private final String sound = "default";
        private final String title = "가격 하락 알림 도착!";
        private final String body;

        private final int priceDropAmount; // 가격 하락량 필드 추가

        public PriceDropPushMessage(String to, String productName, int previousPrice, int newPrice, int priceDropAmount) {
            this.to = to;
            this.priceDropAmount = priceDropAmount;
            this.body = String.format("찜한 상품 '%s'의 가격이 %d원에서 %d원으로 %d원 만큼 떨어졌습니다!", productName.isEmpty() ? "기본 상품명" : productName, previousPrice, newPrice, this.priceDropAmount);
        }

        public String getTo() {
            return to;
        }

        public String getSound() {
            return sound;
        }

        public String getTitle() {
            return title;
        }

        public String getBody() {
            return body;
        }
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
