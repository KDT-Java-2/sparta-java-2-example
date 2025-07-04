package com.sparta.bootcamp.java_2_example.domain.purchase.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sparta.bootcamp.java_2_example.common.enums.PurchaseStatus;
import com.sparta.bootcamp.java_2_example.common.exception.ServiceException;
import com.sparta.bootcamp.java_2_example.domain.product.entity.Product;
import com.sparta.bootcamp.java_2_example.domain.product.repository.ProductRepository;
import com.sparta.bootcamp.java_2_example.domain.purchase.dto.PurchaseProductRequest;
import com.sparta.bootcamp.java_2_example.domain.purchase.entity.Purchase;
import com.sparta.bootcamp.java_2_example.domain.purchase.repository.PurchaseProductRepository;
import com.sparta.bootcamp.java_2_example.domain.purchase.repository.PurchaseRepository;
import com.sparta.bootcamp.java_2_example.domain.user.entity.User;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)  // @SpringBootTest 대신 사용
class PurchaseProcessServiceTest {

  @InjectMocks
  private PurchaseProcessService purchaseProcessService;

  @Mock
  private PurchaseRepository purchaseRepository;

  @Mock
  private ProductRepository productRepository;

  @Mock
  private PurchaseProductRepository purchaseProductRepository;

  private User testUser;
  private Product testProduct;
  private Purchase testPurchase;

  @BeforeEach
  void setUp() {
    testUser = User.builder()
        .name("테스트사용자")
        .email("test@example.com")
        .passwordHash("hashedPassword")
        .build();

    ReflectionTestUtils.setField(testUser, "id", 1L);

    testProduct = Product.builder()
        .name("노트북")
        .price(new BigDecimal("1000000"))
        .stock(10)
        .build();

    ReflectionTestUtils.setField(testProduct, "id", 1L);

    testPurchase = Purchase.builder()
        .user(testUser)
        .totalPrice(BigDecimal.ZERO)
        .status(PurchaseStatus.PENDING)
        .build();

    ReflectionTestUtils.setField(testPurchase, "id", 1L);
  }

  @Test
  @DisplayName("재고가 충분한 상품을 구매하면 재고가 감소하고 구매가 성공한다")
  void process_should_decreaseStockAndSucceed_when_productInStock_aaa() {
    // Arrange
    PurchaseProductRequest purchaseItem = new PurchaseProductRequest();
    ReflectionTestUtils.setField(purchaseItem, "productId", 1L);
    ReflectionTestUtils.setField(purchaseItem, "quantity", 2);

    List<PurchaseProductRequest> purchaseItems = List.of(purchaseItem);

    when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
    when(purchaseRepository.save(any(Purchase.class))).thenReturn(testPurchase);
    when(purchaseProductRepository.saveAll(anyList())).thenReturn(Collections.emptyList());

    // Act
    Purchase result = purchaseProcessService.process(testUser, purchaseItems);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getTotalPrice()).isEqualTo(new BigDecimal("2000000")); // 1,000,000 * 2
    assertThat(testProduct.getStock()).isEqualTo(8); // 10 - 2

    verify(productRepository).findById(1L);
    verify(purchaseRepository).save(any(Purchase.class));
    verify(purchaseProductRepository).saveAll(anyList());
  }

  @Test
  @DisplayName("재고가 충분한 상품을 구매하면 재고가 감소하고 구매가 성공한다")
  void process_should_decreaseStockAndSucceed_when_productInStock_gwt() {
    // Given
    PurchaseProductRequest purchaseItem = new PurchaseProductRequest();
    ReflectionTestUtils.setField(purchaseItem, "productId", 1L);
    ReflectionTestUtils.setField(purchaseItem, "quantity", 2);

    List<PurchaseProductRequest> purchaseItems = List.of(purchaseItem);

    when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
    when(purchaseRepository.save(any(Purchase.class))).thenReturn(testPurchase);
    when(purchaseProductRepository.saveAll(anyList())).thenReturn(Collections.emptyList());

    // When
    Purchase result = purchaseProcessService.process(testUser, purchaseItems);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getTotalPrice()).isEqualTo(new BigDecimal("2000000")); // 1,000,000 * 2
    assertThat(testProduct.getStock()).isEqualTo(8); // 10 - 2

    verify(productRepository).findById(1L);
    verify(purchaseRepository).save(any(Purchase.class));
    verify(purchaseProductRepository).saveAll(anyList());
  }

  @Test
  @DisplayName("존재하지 않는 상품을 구매하려고 하면 NoSuchElementException이 발생한다")
  void process_should_throwNoSuchElementException_when_nonExistentProduct() {
    // Given
    PurchaseProductRequest purchaseItem = new PurchaseProductRequest();
    ReflectionTestUtils.setField(purchaseItem, "productId", 999L); // 존재하지 않는 상품 ID
    ReflectionTestUtils.setField(purchaseItem, "quantity", 1);

    List<PurchaseProductRequest> purchaseItems = List.of(purchaseItem);

    when(productRepository.findById(999L)).thenReturn(Optional.empty());
    when(purchaseRepository.save(any(Purchase.class))).thenReturn(testPurchase);

    // When & Then
    assertThrows(NoSuchElementException.class, () -> {
      purchaseProcessService.process(testUser, purchaseItems);
    });

    // 검증: 상품을 찾으려고 시도했는지 확인
    verify(productRepository).findById(999L);

    // 검증: Purchase는 생성되었지만, PurchaseProduct는 생성되지 않았는지 확인
    verify(purchaseRepository).save(any(Purchase.class));
    verify(purchaseProductRepository, never()).saveAll(anyList());
  }

  // === 경계선 테스트 ===
  @Test
  @DisplayName("재고와 정확히 같은 수량을 구매하면 재고가 0이 된다")
  void process_should_stockBecomesZero_when_exactStockQuantityPurchased() {
    // Given
    PurchaseProductRequest purchaseItem = new PurchaseProductRequest();
    ReflectionTestUtils.setField(purchaseItem, "productId", 1L);
    ReflectionTestUtils.setField(purchaseItem, "quantity", 10); // 재고와 동일한 수량

    List<PurchaseProductRequest> purchaseItems = List.of(purchaseItem);

    when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
    when(purchaseRepository.save(any(Purchase.class))).thenReturn(testPurchase);
    when(purchaseProductRepository.saveAll(anyList())).thenReturn(Collections.emptyList());

    // When
    Purchase result = purchaseProcessService.process(testUser, purchaseItems);

    // Then
    assertThat(result).isNotNull();
    assertThat(testProduct.getStock()).isEqualTo(0); // 재고가 0이 되어야 함

    verify(productRepository).findById(1L);
    verify(purchaseRepository).save(any(Purchase.class));
    verify(purchaseProductRepository).saveAll(anyList());
  }

  // === 실패 케이스 테스트 ===
  @Test
  @DisplayName("재고가 0인 상품을 구매하려고 하면 OUT_OF_STOCK_PRODUCT 예외가 발생한다")
  void process_should_throwsOutOfStockException_when_zeroStock() {
    // Given
    testProduct = Product.builder()
        .name("품절상품")
        .price(new BigDecimal("1000000"))
        .stock(0) // 재고 0
        .build();
    ReflectionTestUtils.setField(testProduct, "id", 1L);

    PurchaseProductRequest purchaseItem = new PurchaseProductRequest();
    ReflectionTestUtils.setField(purchaseItem, "productId", 1L);
    ReflectionTestUtils.setField(purchaseItem, "quantity", 1);

    List<PurchaseProductRequest> purchaseItems = List.of(purchaseItem);

    when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
    when(purchaseRepository.save(any(Purchase.class))).thenReturn(testPurchase); // Mock 설정 추가

    // When & Then
    ServiceException exception = assertThrows(ServiceException.class, () -> {
      purchaseProcessService.process(testUser, purchaseItems);
    });

    assertThat(exception.getCode()).isEqualTo("OUT_OF_STOCK_PRODUCT");
    assertThat(testProduct.getStock()).isEqualTo(0); // 재고는 그대로 0

    verify(productRepository).findById(1L);
    verify(purchaseRepository, times(1)).save(any(Purchase.class)); // Purchase는 저장됨
    verify(purchaseProductRepository, never()).saveAll(anyList()); // PurchaseProduct는 저장되지 않음
  }
}