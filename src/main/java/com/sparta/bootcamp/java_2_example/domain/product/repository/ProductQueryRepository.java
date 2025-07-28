package com.sparta.bootcamp.java_2_example.domain.product.repository;

import static com.sparta.bootcamp.java_2_example.domain.product.entity.QProduct.product;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.bootcamp.java_2_example.domain.product.dto.ProductSearchRequest;
import com.sparta.bootcamp.java_2_example.domain.product.dto.ProductSearchResponse;
import com.sparta.bootcamp.java_2_example.domain.product.dto.QProductSearchResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductQueryRepository {

  private final JPAQueryFactory queryFactory;

  public Page<ProductSearchResponse> searchProduct(ProductSearchRequest request,
      Pageable pageable) {

    BooleanBuilder builder = new BooleanBuilder();
    builder.and(product.deletedYn.eq(false))
        .and(equalCategory(request.getCategoryId()))
        .and(goeMinPrice(request.getMinPrice()))
        .and(loeMaxPrice(request.getMaxPrice()));

    OrderSpecifier<?>[] orderSpecifiers = createOrderSpecifiers(pageable.getSort());

    List<ProductSearchResponse> content = queryFactory
        .select(new QProductSearchResponse(
            product.id,
            product.name,
            product.price,
            product.stock,
            product.createdAt
        ))
        .from(product)
        .where(builder)
        .orderBy(orderSpecifiers)
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    Long totalCount = queryFactory
        .select(product.count())
        .from(product)
        .where(builder)
        .fetchOne();

    return new PageImpl<>(content, pageable, Objects.nonNull(totalCount) ? totalCount : 0L);
  }

  private BooleanExpression equalCategory(Long categoryId) {
    return Objects.nonNull(categoryId) ? product.category.id.eq(categoryId) : null;
  }

  private BooleanExpression goeMinPrice(Integer minPrice) {
    return Objects.nonNull(minPrice) ? product.price.goe(BigDecimal.valueOf(minPrice)) : null;
  }

  private BooleanExpression loeMaxPrice(Integer maxPrice) {
    return Objects.nonNull(maxPrice) ? product.price.loe(BigDecimal.valueOf(maxPrice)) : null;
  }

  private OrderSpecifier<?>[] createOrderSpecifiers(Sort sortBy) {
    if (sortBy.isEmpty()) {
      return new OrderSpecifier[]{product.createdAt.desc()};
    }

    return sortBy.stream()
        .map(order -> {
          String property = order.getProperty();
          boolean isAsc = order.isAscending();

          return switch (property) {
            case "price" -> isAsc ? product.price.asc() : product.price.desc();
            case "createdAt" -> isAsc ? product.createdAt.asc() : product.createdAt.desc();
            case "name" -> isAsc ? product.name.asc() : product.name.desc();
            case "stock" -> isAsc ? product.stock.asc() : product.stock.desc();
            case "id" -> isAsc ? product.id.asc() : product.id.desc();
            default -> product.createdAt.desc();
          };
        })
        .toArray(OrderSpecifier[]::new);
  }
}
