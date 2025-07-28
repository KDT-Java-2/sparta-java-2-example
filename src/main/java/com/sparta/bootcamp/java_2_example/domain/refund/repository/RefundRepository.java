package com.sparta.bootcamp.java_2_example.domain.refund.repository;

import com.sparta.bootcamp.java_2_example.domain.refund.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefundRepository extends JpaRepository<Refund, Long> {

}
