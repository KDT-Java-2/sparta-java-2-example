package com.sparta.bootcamp.java_2_example.domain.coupon.repository;

import com.sparta.bootcamp.java_2_example.domain.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

}
