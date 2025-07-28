package com.sparta.bootcamp.java_2_example.domain.refund.repository;

import com.sparta.bootcamp.java_2_example.domain.refund.entity.RefundProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefundProductRepository extends JpaRepository<RefundProduct, Long> {

}
