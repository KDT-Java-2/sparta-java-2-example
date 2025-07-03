package com.sparta.bootcamp.java_2_example.domain.category.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryRequest {

  String name;

  Long categoryId;

}
