package com.sparta.bootcamp.java_2_example.domain.category.controller;

import com.sparta.bootcamp.java_2_example.common.response.ApiResponse;
import com.sparta.bootcamp.java_2_example.domain.category.dto.CategoryRequest;
import com.sparta.bootcamp.java_2_example.domain.category.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {

  private final CategoryService categoryService;

  @PostMapping
  public ApiResponse<Void> save(@Valid @RequestBody CategoryRequest request) {
    categoryService.save(request);
    return ApiResponse.success();
  }

}
