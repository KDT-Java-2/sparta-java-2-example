package com.sparta.bootcamp.java_2_example.domain.cart.repository;

import com.sparta.bootcamp.java_2_example.domain.cart.entity.Cart;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

  Optional<Cart> findByUser_IdAndProduct_Id(Long userId, Long productId);

  @Query("SELECT c FROM Cart c JOIN FETCH c.product WHERE c.user.id = :userId")
  List<Cart> findByUser_Id(Long userId);

}
