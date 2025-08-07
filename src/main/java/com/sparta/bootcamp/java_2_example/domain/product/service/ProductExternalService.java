package com.sparta.bootcamp.java_2_example.domain.product.service;


import com.sparta.bootcamp.java_2_example.common.exception.ServiceException;
import com.sparta.bootcamp.java_2_example.common.exception.ServiceExceptionCode;
import com.sparta.bootcamp.java_2_example.domain.category.entity.Category;
import com.sparta.bootcamp.java_2_example.domain.category.repository.CategoryRepository;
import com.sparta.bootcamp.java_2_example.domain.product.entity.Product;
import com.sparta.bootcamp.java_2_example.domain.product.repository.ProductRepository;
import com.sparta.bootcamp.java_2_example.global.external.client.ExternalShopClient;
import com.sparta.bootcamp.java_2_example.global.external.dto.ExternalProductResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductExternalService {

  private final ExternalShopClient externalShopClient;

  private final ProductRedisService productRedisService;

  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;

  @Transactional
  @Retryable(value = ServiceException.class, maxAttempts = 10, backoff = @Backoff(delay = 1000))
  public void saveAllExternalProducts() {
    int page = 0;
    int pageSize = 10;
    boolean lastPage = false;

    while (!lastPage) {
      ExternalProductResponse responses = externalShopClient.getProducts(page, pageSize);
      log.info("Response for page {}: {}", page, responses);

      if (Objects.isNull(responses) || Objects.isNull(responses.getMessage())) {
        throw new ServiceException(ServiceExceptionCode.NOT_FOUND_PRODUCT);
      }

      List<ExternalProductResponse.ExternalResponse> contents = responses.getMessage()
          .getContents();

      if (Objects.isNull(contents) || contents.isEmpty()) {
        break;
      }

      Category category = categoryRepository.findById(1L)
          .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_PRODUCT));

      List<Product> products = new ArrayList<>();
      for (ExternalProductResponse.ExternalResponse externalProduct : contents) {
        Product product = Product.builder()
            .name(externalProduct.getName())
            .description(externalProduct.getDescription())
            .stock(externalProduct.getStock())
            .price(externalProduct.getPrice())
            .category(category)
            .build();
        products.add(product);
      }
      List<Product> productEntities = productRepository.saveAll(products);

      for (Product product : productEntities) {
        productRedisService.addProductToSortedSets(product);
      }

      ExternalProductResponse.ExternalPageable pageable = responses.getMessage().getPageable();
      if (Objects.nonNull(pageable)) {
        lastPage = pageable.isLast();
      } else {
        lastPage = contents.size() < pageSize;
      }
      page++;
    }
  }

}