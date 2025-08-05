package com.sparta.bootcamp.java_2_example.domain.coupon.repository;

import com.sparta.bootcamp.java_2_example.domain.coupon.entity.CouponProduct;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponProductRepository extends JpaRepository<CouponProduct, Long> {

  @Query("SELECT cp FROM CouponProduct cp JOIN FETCH cp.coupon JOIN FETCH cp.product WHERE cp.product.id = :productId")
  List<CouponProduct> findAllByProduct_Id(Long productId);
}
