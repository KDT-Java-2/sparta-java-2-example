package com.sparta.bootcamp.java_2_example.domain.product.service;

import com.sparta.bootcamp.java_2_example.domain.product.dto.ProductDiscountResponse;
import com.sparta.bootcamp.java_2_example.domain.product.dto.ProductSearchResponse;
import com.sparta.bootcamp.java_2_example.domain.product.entity.Product;
import com.sparta.bootcamp.java_2_example.domain.product.mapper.ProductMapper;
import com.sparta.bootcamp.java_2_example.domain.product.repository.ProductRepository;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductRedisService {

  private final RedisTemplate<String, Object> redisTemplate;
  private final ProductRepository productRepository;
  private final ProductMapper productMapper;

  private static final String PRODUCT_PRICE_SORT_KEY = "product:sort:price";
  private static final String PRODUCT_CREATED_AT_SORT_KEY = "product:sort:createdAt";
  private static final String PRODUCT_RATING_SORT_KEY = "product:sort:rating";

  public void addProductToSortedSets(Product product) {
    ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();

    String productId = product.getId().toString();

    double priceScore = product.getPrice().doubleValue();
    zSetOps.add(PRODUCT_PRICE_SORT_KEY, productId, priceScore);

    double createdAtScore = product.getCreatedAt().toEpochSecond(ZoneOffset.UTC);
    zSetOps.add(PRODUCT_CREATED_AT_SORT_KEY, productId, createdAtScore);

    double ratingScore =
        product.getAverageRating() != null ? product.getAverageRating().doubleValue() : 0.0;
    zSetOps.add(PRODUCT_RATING_SORT_KEY, productId, ratingScore);

    log.info("Product {} added to Redis sorted sets", productId);
  }


  public Page<ProductSearchResponse> getSortedProducts(String sort, String order,
      Long categoryId, Integer minPrice, Integer maxPrice,
      Pageable pageable) {

    String sortKey = getSortKey(sort);

    if (sortKey == null) {
      throw new IllegalArgumentException("Invalid sort criteria: " + sort);
    }

    ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();

    Set<Object> productIds;
    if ("desc".equalsIgnoreCase(order)) {
      productIds = zSetOps.reverseRange(sortKey, 0, -1);
    } else {
      productIds = zSetOps.range(sortKey, 0, -1);
    }

    if (productIds == null || productIds.isEmpty()) {
      return new PageImpl<>(List.of(), pageable, 0);
    }

    List<Long> productIdList = productIds.stream()
        .map(id -> Long.valueOf(id.toString()))
        .collect(Collectors.toList());

    List<Long> filteredProductIds = filterProducts(productIdList, categoryId, minPrice, maxPrice);

    int start = (int) pageable.getOffset();
    int end = Math.min(start + pageable.getPageSize(), filteredProductIds.size());

    if (start >= filteredProductIds.size()) {
      return new PageImpl<>(List.of(), pageable, filteredProductIds.size());
    }

    List<Long> pagedProductIds = filteredProductIds.subList(start, end);

    List<ProductSearchResponse> products = pagedProductIds.stream()
        .map(productId -> {
          Product product = productRepository.findById(productId).orElse(null);
          if (product == null) {
            return null;
          }

          ProductSearchResponse response = productMapper.toProductSearchResponse(product);

          if (product.getAverageRating() != null) {
            response.setRating(product.getAverageRating());
          }

          return response;
        })
        .filter(product -> product != null)
        .collect(Collectors.toList());

    return new PageImpl<>(products, pageable, filteredProductIds.size());
  }

  private String getSortKey(String sort) {
    return switch (sort.toLowerCase()) {
      case "price" -> PRODUCT_PRICE_SORT_KEY;
      case "createdat" -> PRODUCT_CREATED_AT_SORT_KEY;
      case "rating" -> PRODUCT_RATING_SORT_KEY;
      default -> null;
    };
  }

  private List<Long> filterProducts(List<Long> productIds, Long categoryId, Integer minPrice,
      Integer maxPrice) {
    return productIds.stream()
        .filter(productId -> {
          Product product = productRepository.findById(productId).orElse(null);

          if (product == null) {
            return false;
          }

          if (categoryId != null && !categoryId.equals(product.getCategory().getId())) {
            return false;
          }

          if (minPrice != null && product.getPrice().compareTo(BigDecimal.valueOf(minPrice)) < 0) {
            return false;
          }

          if (maxPrice != null && product.getPrice().compareTo(BigDecimal.valueOf(maxPrice)) > 0) {
            return false;
          }

          return true;
        })
        .collect(Collectors.toList());
  }

  public ProductDiscountResponse getCachedProductDiscount(String cacheKey) {
    try {
      ValueOperations<String, Object> valueOps = redisTemplate.opsForValue();
      Object cachedData = valueOps.get(cacheKey);

      if (cachedData != null) {
        return (ProductDiscountResponse) cachedData;
      }
    } catch (Exception e) {
      log.warn("Failed to retrieve cached product discount data for key: {}, error: {}",
          cacheKey, e.getMessage());
    }
    return null;
  }

  public void cacheProductDiscount(String cacheKey, ProductDiscountResponse response,
      Duration ttl) {
    try {
      ValueOperations<String, Object> valueOps = redisTemplate.opsForValue();
      valueOps.set(cacheKey, response, ttl);
    } catch (Exception e) {
      log.warn("Failed to cache product discount data for key: {}, error: {}",
          cacheKey, e.getMessage());
    }
  }

  // 캐시 무효화 메소드 (쿠폰이나 상품 정보가 변경될 때 사용)
  public void evictProductDiscountCache(Long productId) {
    try {
      String cacheKey = "product:" + productId + ":max_discount_percentage";
      redisTemplate.delete(cacheKey);
      log.info("Product discount cache evicted for product ID: {}", productId);
    } catch (Exception e) {
      log.warn("Failed to evict product discount cache for product ID: {}, error: {}",
          productId, e.getMessage());
    }
  }
} 