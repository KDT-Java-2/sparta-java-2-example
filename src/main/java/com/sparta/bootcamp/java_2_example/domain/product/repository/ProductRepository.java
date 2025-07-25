package com.sparta.bootcamp.java_2_example.domain.product.repository;

import com.sparta.bootcamp.java_2_example.domain.product.entity.Product;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

  List<Product> findAllByStockGreaterThan(Integer stock);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT p FROM Product p WHERE p.id = :id")
  Optional<Product> findByIdForUpdate(Long id);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<Product> findFirstByName(String name);

  @Lock(LockModeType.OPTIMISTIC)
  Optional<Product> findByIdOrderById(Long productId);

}
