package com.sparta.bootcamp.java_2_example.domain.product.mapper;

import com.sparta.bootcamp.java_2_example.domain.category.entity.Category;
import com.sparta.bootcamp.java_2_example.domain.coupon.entity.Coupon;
import com.sparta.bootcamp.java_2_example.domain.product.dto.ProductDiscountResponse;
import com.sparta.bootcamp.java_2_example.domain.product.dto.ProductRequest;
import com.sparta.bootcamp.java_2_example.domain.product.dto.ProductResponse;
import com.sparta.bootcamp.java_2_example.domain.product.dto.ProductSearchResponse;
import com.sparta.bootcamp.java_2_example.domain.product.entity.Product;
import java.math.BigDecimal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

  @Mapping(target = "category.id", source = "category.id")
  @Mapping(target = "category.name", source = "category.name")
  ProductResponse toProductResponse(Product product);

  @Mapping(target = "productId", source = "product.id")
  @Mapping(target = "couponId", source = "coupon.id")
  @Mapping(target = "couponName", source = "coupon.name")
  @Mapping(target = "discountType", source = "coupon.discountType")
  @Mapping(target = "totalDiscountAmount", source = "totalDiscountAmount")
  ProductDiscountResponse toMaxDiscountResponse(Product product, Coupon coupon
      , BigDecimal totalDiscountAmount);

  @Mapping(target = "id", source = "id")
  @Mapping(target = "name", source = "name")
  @Mapping(target = "price", source = "price")
  @Mapping(target = "stock", source = "stock")
  @Mapping(target = "createdAt", source = "createdAt")
  ProductSearchResponse toProductSearchResponse(Product product);

  @Mapping(target = "category", source = "category")
  @Mapping(target = "name", source = "request.name")
  @Mapping(target = "description", source = "request.description")
  Product toProduct(ProductRequest request, Category category);

}
