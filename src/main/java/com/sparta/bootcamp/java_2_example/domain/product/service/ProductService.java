package com.sparta.bootcamp.java_2_example.domain.product.service;

import com.sparta.bootcamp.java_2_example.common.enums.DiscountType;
import com.sparta.bootcamp.java_2_example.common.enums.PurchaseStatus;
import com.sparta.bootcamp.java_2_example.common.exception.ServiceException;
import com.sparta.bootcamp.java_2_example.common.exception.ServiceExceptionCode;
import com.sparta.bootcamp.java_2_example.domain.category.entity.Category;
import com.sparta.bootcamp.java_2_example.domain.category.repository.CategoryRepository;
import com.sparta.bootcamp.java_2_example.domain.coupon.entity.Coupon;
import com.sparta.bootcamp.java_2_example.domain.coupon.entity.CouponProduct;
import com.sparta.bootcamp.java_2_example.domain.coupon.repository.CouponProductRepository;
import com.sparta.bootcamp.java_2_example.domain.product.dto.ProductCreateResponse;
import com.sparta.bootcamp.java_2_example.domain.product.dto.ProductDiscountResponse;
import com.sparta.bootcamp.java_2_example.domain.product.dto.ProductRequest;
import com.sparta.bootcamp.java_2_example.domain.product.dto.ProductResponse;
import com.sparta.bootcamp.java_2_example.domain.product.dto.ProductSearchRequest;
import com.sparta.bootcamp.java_2_example.domain.product.dto.ProductSearchResponse;
import com.sparta.bootcamp.java_2_example.domain.product.dto.ProductUpdateRequest;
import com.sparta.bootcamp.java_2_example.domain.product.entity.Product;
import com.sparta.bootcamp.java_2_example.domain.product.mapper.ProductMapper;
import com.sparta.bootcamp.java_2_example.domain.product.repository.ProductQueryRepository;
import com.sparta.bootcamp.java_2_example.domain.product.repository.ProductRepository;
import com.sparta.bootcamp.java_2_example.domain.purchase.repository.PurchaseProductRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductMapper productMapper;

  private final ProductRepository productRepository;
  private final ProductQueryRepository productQueryRepository;
  private final CategoryRepository categoryRepository;
  private final PurchaseProductRepository purchaseProductRepository;
  private final CouponProductRepository couponProductRepository;
  private final ProductRedisService productRedisService;

  @Transactional(readOnly = true)
  public Page<ProductSearchResponse> searchProduct(ProductSearchRequest searchRequest,
      Pageable pageable) {

    if (searchRequest.getSort() != null && !searchRequest.getSort().isEmpty()) {
      return productRedisService.getSortedProducts(
          searchRequest.getSort(),
          searchRequest.getOrder() != null ? searchRequest.getOrder() : "asc",
          searchRequest.getCategoryId(),
          searchRequest.getMinPrice(),
          searchRequest.getMaxPrice(),
          pageable
      );
    }

    return productQueryRepository.searchProduct(searchRequest, pageable);
  }

  @Transactional(readOnly = true)
  public ProductResponse getProductById(Long id) {
    Product product = productRepository.findById(id)
        .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_PRODUCT));

    return productMapper.toProductResponse(product);
  }

  @Transactional(readOnly = true)
  public ProductDiscountResponse getProductByMaxDiscount(Long id) {
    String cacheKey = "product:" + id + ":max_discount_percentage";

    ProductDiscountResponse cachedResponse = productRedisService.getCachedProductDiscount(cacheKey);
    if (Objects.nonNull(cachedResponse)) {
      return cachedResponse;
    }

    List<CouponProduct> couponProducts = couponProductRepository.findAllByProduct_Id(id);

    if (couponProducts.isEmpty()) {
      throw new ServiceException(ServiceExceptionCode.NOT_FOUND_COUPON);
    }

    Coupon maxCoupon = null;
    BigDecimal totalDiscountAmount = BigDecimal.ZERO;
    Product product = couponProducts.get(0).getProduct();
    BigDecimal productPrice = product.getPrice();

    LocalDateTime now = LocalDateTime.now();

    for (CouponProduct couponProduct : couponProducts) {
      Coupon coupon = couponProduct.getCoupon();

      if (!isCouponValid(coupon, now, productPrice)) {
        continue;
      }

      BigDecimal discountAmount = calculateDiscountAmount(coupon, productPrice);

      if (discountAmount.compareTo(totalDiscountAmount) > 0) {
        totalDiscountAmount = discountAmount;
        maxCoupon = coupon;
      }
    }

    ProductDiscountResponse response = productMapper.toMaxDiscountResponse(product, maxCoupon,
        totalDiscountAmount);

    productRedisService.cacheProductDiscount(cacheKey, response, Duration.ofHours(1));
    return response;
  }

  private boolean isCouponValid(Coupon coupon, LocalDateTime now, BigDecimal productPrice) {
    if (now.isBefore(coupon.getStartDate()) || now.isAfter(coupon.getEndDate())) {
      return false;
    }
    if (coupon.getUsageLimit() != null && coupon.getUsedCount() >= coupon.getUsageLimit()) {
      return false;
    }
    if (coupon.getMinOrderAmount() != null &&
        productPrice.compareTo(coupon.getMinOrderAmount()) < 0) {
      return false;
    }
    return true;
  }

  private BigDecimal calculateDiscountAmount(Coupon coupon, BigDecimal productPrice) {
    BigDecimal discountAmount;

    if (coupon.getDiscountType() == DiscountType.FIXED_AMOUNT) {
      // 고정 할인
      discountAmount = coupon.getDiscountValue();
    } else {
      // 퍼센트 할인
      discountAmount = productPrice.multiply(coupon.getDiscountValue())
          .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    // 최대 할인 금액 제한 적용
    if (coupon.getMaxDiscountAmount() != null &&
        discountAmount.compareTo(coupon.getMaxDiscountAmount()) > 0) {
      discountAmount = coupon.getMaxDiscountAmount();
    }

    // 할인 금액이 상품 가격을 초과하지 않도록 제한
    if (discountAmount.compareTo(productPrice) > 0) {
      discountAmount = productPrice;
    }

    return discountAmount;
  }

  @Transactional
  public ProductCreateResponse create(ProductRequest request) {
    Category category = categoryRepository.findById(request.getCategoryId())
        .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_CATEGORY));

    Product product = productRepository.save(productMapper.toProduct(request, category));

    // Redis에 상품 추가
    productRedisService.addProductToSortedSets(product);

    return ProductCreateResponse.builder()
        .productId(product.getId())
        .build();
  }

  @Transactional
  public ProductResponse update(Long productId, ProductUpdateRequest request) {
    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_PRODUCT));

    if (Objects.nonNull(request.getCategoryId())) {
      Category category = categoryRepository.findById(request.getCategoryId())
          .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_CATEGORY));

      product.setCategory(category);
    }
    product.setName(request.getName());
    product.setDescription(request.getDescription());
    product.setPrice(request.getPrice());
    product.setStock(request.getStock());

    Product savedProduct = productRepository.save(product);

    // Redis에 상품 업데이트
    productRedisService.addProductToSortedSets(savedProduct);

    return productMapper.toProductResponse(savedProduct);
  }

  @Transactional
  public void delete(Long id) {
    Product product = productRepository.findById(id)
        .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_PRODUCT));

    boolean hasCompletedPurchase = purchaseProductRepository
        .existsByProductIdAndPurchaseStatus(id, PurchaseStatus.COMPLETED);

    if (hasCompletedPurchase) {
      throw new ServiceException(ServiceExceptionCode.CHECK_STATUS_PURCHASE);
    }

    product.setDeletedYn(true);
  }
}
