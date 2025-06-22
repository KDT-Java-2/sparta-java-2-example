package com.sparta.bootcamp.java_2_example.domain.product.entity;

import com.sparta.bootcamp.java_2_example.domain.category.entity.Category;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id")
  private Category category;

  @Column(nullable = false)
  private String name;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(nullable = false)
  private BigDecimal price;

  @Column(nullable = false)
  private Integer stock;

  @Column(nullable = false, updatable = false)
  @CreationTimestamp
  LocalDateTime createdAt;

  @Column(nullable = false)
  @UpdateTimestamp
  LocalDateTime updatedAt;

  @Builder
  public Product(
      Category category,
      String name,
      String description,
      BigDecimal price,
      Integer stock
  ) {
    this.category = category;
    this.name = name;
    this.description = description;
    this.price = price;
    this.stock = stock;
  }
}
