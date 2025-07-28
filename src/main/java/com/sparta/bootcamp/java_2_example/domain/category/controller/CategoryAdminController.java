package com.sparta.bootcamp.java_2_example.domain.category.controller;

import com.sparta.bootcamp.java_2_example.common.response.ApiResponse;
import com.sparta.bootcamp.java_2_example.domain.category.dto.CategoryRequest;
import com.sparta.bootcamp.java_2_example.domain.category.dto.CategoryResponse;
import com.sparta.bootcamp.java_2_example.domain.category.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/categories")
public class CategoryAdminController {

  private final CategoryService categoryService;

  @PostMapping
  public ApiResponse<Void> create(@Valid @RequestBody CategoryRequest request) {
    categoryService.create(request);
    return ApiResponse.success();
  }

  @PutMapping("/{categoryId}")
  public ApiResponse<CategoryResponse> update(@PathVariable Long categoryId,
      @Valid @RequestBody CategoryRequest request) {
    return ApiResponse.success(categoryService.update(categoryId, request));
  }

  @DeleteMapping("/{categoryId}")
  public ApiResponse<Void> delete(@PathVariable Long categoryId) {
    categoryService.delete(categoryId);
    return ApiResponse.success();
  }

}
