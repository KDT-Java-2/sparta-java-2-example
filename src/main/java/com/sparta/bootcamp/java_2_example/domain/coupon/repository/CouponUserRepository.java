package com.sparta.bootcamp.java_2_example.domain.coupon.repository;

import com.sparta.bootcamp.java_2_example.domain.coupon.entity.CouponUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponUserRepository extends JpaRepository<CouponUser, Integer> {

  @Query("SELECT cu FROM CouponUser cu JOIN FETCH cu.coupon WHERE cu.code = :code")
  Optional<CouponUser> findByCode(String code);

}
