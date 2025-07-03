package com.sparta.bootcamp.java_2_example.domain.category.repository;

import com.sparta.bootcamp.java_2_example.domain.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

}
