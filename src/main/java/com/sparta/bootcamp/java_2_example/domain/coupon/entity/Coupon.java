package com.sparta.bootcamp.java_2_example.domain.coupon.entity;

import com.sparta.bootcamp.java_2_example.common.enums.DiscountType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

@Table
@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Coupon {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(nullable = false)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private DiscountType discountType;

  @Column(nullable = false)
  private BigDecimal discountValue;

  @Column
  private BigDecimal minOrderAmount;

  @Column
  private BigDecimal maxDiscountAmount;

  @Column(nullable = false)
  private LocalDateTime startDate;

  @Column(nullable = false)
  private LocalDateTime endDate;

  @Column
  private Integer usageLimit;

  @Column(nullable = false)
  private Integer issueCount;

  @Column(nullable = false)
  private Integer usedCount;

  @Column(nullable = false, updatable = false)
  @CreationTimestamp
  LocalDateTime createdAt;

  @Column(nullable = false)
  @UpdateTimestamp
  LocalDateTime updatedAt;

  @Builder
  public Coupon(
      String name,
      DiscountType discountType,
      BigDecimal discountValue,
      BigDecimal minOrderAmount,
      BigDecimal maxDiscountAmount,
      LocalDateTime startDate,
      LocalDateTime endDate,
      Integer usageLimit,
      Integer issueCount,
      Integer usedCount
  ) {
    this.name = name;
    this.discountType = discountType;
    this.discountValue = discountValue;
    this.minOrderAmount = minOrderAmount;
    this.maxDiscountAmount = maxDiscountAmount;
    this.startDate = startDate;
    this.endDate = endDate;
    this.usageLimit = usageLimit;
    this.issueCount = issueCount;
    this.usedCount = usedCount;
  }
}
