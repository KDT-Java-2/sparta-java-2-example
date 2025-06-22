package com.sparta.bootcamp.java_2_example.domain.purchase.repository;

import com.sparta.bootcamp.java_2_example.domain.purchase.entity.PurchaseProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseProductRepository extends JpaRepository<PurchaseProduct, Long> {

}
