package com.sparta.bootcamp.java_2_example.domain.category.service;

import com.sparta.bootcamp.java_2_example.common.exception.ServiceException;
import com.sparta.bootcamp.java_2_example.common.exception.ServiceExceptionCode;
import com.sparta.bootcamp.java_2_example.domain.category.dto.CategoryHierarchyResponse;
import com.sparta.bootcamp.java_2_example.domain.category.dto.CategoryRequest;
import com.sparta.bootcamp.java_2_example.domain.category.dto.CategoryResponse;
import com.sparta.bootcamp.java_2_example.domain.category.entity.Category;
import com.sparta.bootcamp.java_2_example.domain.category.mapper.CategoryMapper;
import com.sparta.bootcamp.java_2_example.domain.category.repository.CategoryRepository;
import com.sparta.bootcamp.java_2_example.domain.product.repository.ProductRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {

  private final CategoryMapper categoryMapper;

  private final CategoryRepository categoryRepository;
  private final ProductRepository productRepository;

  @Transactional
  public List<CategoryHierarchyResponse> getHierarchy() {
    List<Category> categories = categoryRepository.findAll();

    Map<Long, CategoryHierarchyResponse> categoryResponseMap = new HashMap<>();

    for (Category category : categories) {
      CategoryHierarchyResponse response = CategoryHierarchyResponse.builder()
          .id(category.getId())
          .name(category.getName())
          .description(category.getDescription())
          .categories(new ArrayList<>())
          .build();
      categoryResponseMap.put(category.getId(), response);
    }

    List<CategoryHierarchyResponse> rootCategories = new ArrayList<>();
    for (Category category : categories) {
      CategoryHierarchyResponse categoryResponse = categoryResponseMap.get(category.getId());

      if (ObjectUtils.isEmpty(category.getParent())) {
        rootCategories.add(categoryResponse);
      } else {
        CategoryHierarchyResponse parentResponse = categoryResponseMap.get(
            category.getParent().getId());
        if (parentResponse != null) {
          parentResponse.getCategories().add(categoryResponse);
        }
      }
    }
    return rootCategories;
  }

  @Transactional
  public void create(CategoryRequest request) {
    Category parentCategory = getParentCategory(request.getParentId());

    categoryRepository.save(Category.builder()
        .name(request.getName())
        .description(request.getDescription())
        .parent(parentCategory)
        .build());
  }

  @Transactional
  public CategoryResponse update(Long categoryId, CategoryRequest request) {
    Category category = categoryRepository.findById(categoryId)
        .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_CATEGORY));

    Category parentCategory = getParentCategory(request.getParentId());

    category.setName(request.getName());
    category.setDescription(request.getDescription());
    category.setParent(parentCategory);

    categoryRepository.save(category);
    return categoryMapper.toResponse(category);
  }

  @Transactional
  public void delete(Long categoryId) {
    Category category = categoryRepository.findById(categoryId)
        .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_CATEGORY));

    boolean hasParentCategory = categoryRepository.existsByParent_Id(category.getId());
    boolean hasProductCategory = productRepository.existsByCategory_Id(category.getId());

    if (hasParentCategory || hasProductCategory) {
      throw new ServiceException(ServiceExceptionCode.NOT_DELETED_CATEGORY);
    }

    categoryRepository.delete(category);
  }

  private Category getParentCategory(Long parentId) {
    return Optional.ofNullable(parentId)
        .flatMap(categoryRepository::findById)
        .orElse(null);
  }

}
