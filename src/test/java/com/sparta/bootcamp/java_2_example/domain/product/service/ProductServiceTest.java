package com.sparta.bootcamp.java_2_example.domain.product.service;

import com.sparta.bootcamp.java_2_example.common.exception.CustomCheckedException;
import com.sparta.bootcamp.java_2_example.domain.product.entity.Product;
import com.sparta.bootcamp.java_2_example.domain.product.repository.ProductRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProductServiceTest {

  private static final Logger log = LoggerFactory.getLogger(ProductLockServiceTest.class);

  @Autowired
  private ProductService productService;

  @Autowired
  private ProductRepository productRepository;

  @Test
  @DisplayName("체크 예외 발생 시 rollbackFor 설정으로 트랜잭션이 롤백된다")
  void testRollbackForCheckedException() {
    // given: 초기 데이터 준비
    Long productId = 1L;
    Product originalProduct = productRepository.findById(productId).orElseThrow();
    BigDecimal originalPrice = originalProduct.getPrice();

    // when & then: 음수 가격 업데이트 시도 -> CustomCheckedException 발생을 기대
    Assertions.assertThrows(CustomCheckedException.class, () -> {
      productService.updateProductQuantity(productId, -1000);
    });

    // then: 롤백이 정상적으로 수행되어 가격이 원래대로 복구되었는지 확인
    Product productAfterRollback = productRepository.findById(productId).orElseThrow();
    Assertions.assertEquals(originalPrice, productAfterRollback.getPrice());
    log.info("롤백 후 가격: {}, 정상 복구됨.", productAfterRollback.getPrice());
  }
}