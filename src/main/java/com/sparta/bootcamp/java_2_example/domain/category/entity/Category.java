package com.sparta.bootcamp.java_2_example.domain.category.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
public class Category {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @JsonBackReference
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id")
  private Category parent;

  @Column(nullable = false, updatable = false)
  @CreationTimestamp
  LocalDateTime createdAt;

  @Column(nullable = false)
  @UpdateTimestamp
  LocalDateTime updatedAt;

  @Builder
  public Category(
      String name,
      Category parent
  ) {
    this.name = name;
    this.parent = parent;
  }
  
}
