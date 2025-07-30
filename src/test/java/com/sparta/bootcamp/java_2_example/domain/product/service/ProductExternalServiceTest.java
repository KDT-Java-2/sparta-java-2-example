package com.sparta.bootcamp.java_2_example.domain.product.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class ProductExternalServiceTest {

  @Autowired
  ProductExternalService productExternalService;

  @Test
  void save() {
    productExternalService.save();
  }

  @Test
  void saveAllExternalProducts() {
    productExternalService.saveAllExternalProducts();
  }

}