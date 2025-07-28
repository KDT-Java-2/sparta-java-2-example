package com.sparta.bootcamp.java_2_example.domain.category.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class CategoryRequest {

  @NotBlank
  String name;

  String description;

  Long parentId;

}
