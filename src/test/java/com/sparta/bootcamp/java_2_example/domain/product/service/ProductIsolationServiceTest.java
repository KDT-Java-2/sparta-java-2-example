package com.sparta.bootcamp.java_2_example.domain.product.service;

import com.sparta.bootcamp.java_2_example.domain.product.entity.Product;
import com.sparta.bootcamp.java_2_example.domain.product.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Transactional
@SpringBootTest
class ProductIsolationServiceTest {

  private static final Logger log = LoggerFactory.getLogger(ProductIsolationServiceTest.class);

  @Autowired
  private ProductIsolationService productService;
  @Autowired
  private ProductRepository productRepository;

  @Test
  @DisplayName("READ_UNCOMMITTED에서는 Dirty Read가 발생한다")
  void testDirtyReadAllowed() throws InterruptedException {
    // Given: 초기 재고는 20
    Long productId = 1L;

    // Thread A: 재고를 10으로 바꾸고 롤백 예정
    Thread threadA = new Thread(() -> {
      productService.updateStockAndForceRollback(productId, 10);
    });

    // Thread B: Thread A가 작업하는 도중에 재고 조회
    Thread threadB = new Thread(() -> {
      try {
        // Thread A가 재고를 변경할 시간을 줌
        Thread.sleep(1000);
        int stock = productService.getStockWithDirtyRead(productId);
        log.info(">>>> Dirty Read 발생: 읽은 재고 = {}", stock);

        Assertions.assertEquals(10, stock); // 10이라는 더티 데이터를 읽었는지 확인
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    });

    // When
    threadA.start();
    threadB.start();
    threadA.join();
    threadB.join();

    // Then: Thread A가 롤백되었으므로 최종 재고는 원상 복구되어야 함
    Product finalProduct = productRepository.findById(productId).orElseThrow();
    log.info(">>>> 최종 실제 재고 = {}", finalProduct.getStock());
    Assertions.assertEquals(20, finalProduct.getStock());
  }

  @Test
  @DisplayName("READ_COMMITTED에서는 Dirty Read가 방지된다")
  void testDirtyReadPrevented() throws InterruptedException {
    // Given: 초기 재고는 20
    Long productId = 1L;

    // Thread A: 재고를 10으로 바꾸고 롤백 예정
    Thread threadA = new Thread(() -> {
      productService.updateStockAndForceRollback(productId, 10);
    });

    // Thread B: Thread A가 작업하는 도중에 재고 조회
    Thread threadB = new Thread(() -> {
      try {
        // Thread A가 재고를 변경할 시간을 줌
        Thread.sleep(1000);
        int stock = productService.getStockWithReadCommitted(productId);
        log.info(">>>> Read Committed: 읽은 재고 = {}", stock);
        Assertions.assertEquals(20, stock); // 원래 데이터를 읽었는지 확인
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    });

    // When
    threadA.start();
    threadB.start();
    threadA.join();
    threadB.join();

    // Then: 최종 재고는 당연히 원상 복구되어야 함
    Product finalProduct = productRepository.findById(productId).orElseThrow();
    log.info(">>>> 최종 실제 재고 = {}", finalProduct.getStock());
    Assertions.assertEquals(20, finalProduct.getStock());
  }


  @Test
  @DisplayName("READ_COMMITTED에서는 Non-Repeatable Read가 발생한다")
  void testNonRepeatableReadAllowed() throws InterruptedException {
    // Given: 초기 재고는 20
    Long productId = 1L;

    // Thread A: 데이터를 두 번 읽는 긴 트랜잭션
    Thread threadA = new Thread(() -> {
      productService.demonstrateNonRepeatableRead(productId);
    });

    // Thread B: 중간에 데이터를 수정하는 짧은 트랜잭션
    Thread threadB = new Thread(() -> {
      try {
        Thread.sleep(1000); // Thread A가 첫 번째 읽기를 수행할 때까지 대기
        productService.updateStock(productId, 5);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    });

    // When
    threadA.start();
    threadB.start();
    threadA.join();
    threadB.join();

    // Then: 최종 재고는 5가 되어야 함
    Product finalProduct = productRepository.findById(productId).orElseThrow();
    Assertions.assertEquals(5, finalProduct.getStock());
  }

  @Test
  @DisplayName("REPEATABLE_READ에서는 Non-Repeatable Read가 방지된다")
  void testNonRepeatableReadPrevented() throws InterruptedException {
    // Given: 초기 재고는 20
    Long productId = 1L;

    // Thread A: REPEATABLE_READ로 데이터를 두 번 읽는 긴 트랜잭션
    Thread threadA = new Thread(() -> {
      productService.demonstrateRepeatableRead(productId);
    });

    // Thread B: 중간에 데이터를 수정하는 짧은 트랜잭션
    Thread threadB = new Thread(() -> {
      try {
        Thread.sleep(1000);
        productService.updateStock(productId, 5);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    });

    // When
    threadA.start();
    threadB.start();
    threadA.join();
    threadB.join();

    // Then: 최종 재고는 5가 되어야 함
    Product finalProduct = productRepository.findById(productId).orElseThrow();
    Assertions.assertEquals(5, finalProduct.getStock());
  }

  @Test
  @DisplayName("REPEATABLE_READ에서는 Phantom Read가 발생할 수 있다")
  void testPhantomReadOccurs() throws InterruptedException {
    // Thread A: 특정 범위의 데이터를 두 번 읽는 긴 트랜잭션
    Thread threadA = new Thread(() -> {
      productService.demonstratePhantomRead();
    });

    // Thread B: 중간에 그 범위에 해당하는 데이터를 추가하는 트랜잭션
    Thread threadB = new Thread(() -> {
      try {
        Thread.sleep(1000);
        productService.insertNewProduct("유령신상품", 20); // stock > 5 조건 만족
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    });

    // When
    threadA.start();
    threadB.start();
    threadA.join();
    threadB.join();
  }

  @Test
  @DisplayName("SERIALIZABLE에서는 Phantom Read가 방지된다")
  void testPhantomReadPrevented() throws InterruptedException {
    // Thread A: SERIALIZABLE로 특정 범위를 두 번 읽는 긴 트랜잭션
    Thread threadA = new Thread(() -> {
      productService.demonstrateSerializable();
    });

    // Thread B: 중간에 데이터를 추가하려고 시도
    Thread threadB = new Thread(() -> {
      try {
        Thread.sleep(1000);
        // 이 메서드는 Thread A가 끝날 때까지 대기(block) 상태에 빠짐
        productService.insertNewProduct("유령신상품", 20);
      } catch (Exception e) {
        // DB에 따라 LockWaitTimeoutException 등이 발생할 수 있음
        log.info("Thread B: 헉! 락 때문에 작업을 못했어요! " + e.getMessage());
      }
    });

    // When
    threadA.start();
    threadB.start();
    threadA.join();
    threadB.join();
  }

}