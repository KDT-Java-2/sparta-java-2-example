package com.sparta.bootcamp.java_2_example.domain.product.repository;

import com.sparta.bootcamp.java_2_example.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

}
