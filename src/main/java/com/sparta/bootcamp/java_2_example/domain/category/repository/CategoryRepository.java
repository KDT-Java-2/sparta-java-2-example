package com.sparta.bootcamp.java_2_example.domain.category.repository;

import com.sparta.bootcamp.java_2_example.domain.category.entity.Category;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

  Boolean existsByParent_Id(Long parentId);

  Optional<Category> findByName(String name);

}
