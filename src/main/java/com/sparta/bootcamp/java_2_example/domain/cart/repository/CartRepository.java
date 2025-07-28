package com.sparta.bootcamp.java_2_example.domain.cart.repository;

import com.sparta.bootcamp.java_2_example.domain.refund.entity.RefundProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<RefundProduct, Long> {

}
