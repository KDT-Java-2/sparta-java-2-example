package com.sparta.bootcamp.java_2_example.domain.purchase.repository;

import com.sparta.bootcamp.java_2_example.domain.purchase.entity.Purchase;
import io.lettuce.core.dynamic.annotation.Param;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

  Optional<Purchase> findByIdAndUser_Id(Long id, Long userId);

  @Modifying(clearAutomatically = true)
  @Query("UPDATE Purchase p SET p.status = 'COMPLETED' WHERE p.createdAt < :date AND p.status = 'PENDING'")
  int bulkUpdateStatus(@Param("date") LocalDateTime date);

}
