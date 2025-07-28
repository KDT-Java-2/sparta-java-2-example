package com.sparta.bootcamp.java_2_example.domain.product.mapper;

import com.sparta.bootcamp.java_2_example.domain.category.entity.Category;
import com.sparta.bootcamp.java_2_example.domain.product.dto.ProductRequest;
import com.sparta.bootcamp.java_2_example.domain.product.dto.ProductResponse;
import com.sparta.bootcamp.java_2_example.domain.product.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

  @Mapping(target = "category.id", source = "category.id")
  @Mapping(target = "category.name", source = "category.name")
  ProductResponse toProductResponse(Product product);

  @Mapping(target = "category", source = "category")
  @Mapping(target = "name", source = "request.name")
  @Mapping(target = "description", source = "request.description")
  Product toProduct(ProductRequest request, Category category);

}
