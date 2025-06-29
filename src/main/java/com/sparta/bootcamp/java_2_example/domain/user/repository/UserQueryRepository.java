package com.sparta.bootcamp.java_2_example.domain.user.repository;

import static com.sparta.bootcamp.java_2_example.domain.purchase.entity.QPurchase.purchase;
import static com.sparta.bootcamp.java_2_example.domain.user.entity.QUser.user;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.bootcamp.java_2_example.domain.user.dto.QUserResponse;
import com.sparta.bootcamp.java_2_example.domain.user.dto.UserResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class UserQueryRepository {

  private final JPAQueryFactory queryFactory;

  public Page<UserResponse> findUserByUserId(Long userId, String email, Pageable pageable) {
    List<UserResponse> contents = queryFactory.select(new QUserResponse(
            user.id,
            user.name,
            user.email,
            purchase.createdAt
        ))
        .from(user)
        .join(purchase).on(purchase.user.eq(user))
        .where(
            user.id.eq(userId),
            emailContains(email)
        )
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    Long totalCount = queryFactory.select(user.count())
        .from(user)
        .join(purchase).on(purchase.user.eq(user))
        .where(
            user.id.eq(userId),
            emailContains(email)
        )
        .fetchOne();

    return new PageImpl<>(contents, pageable, totalCount);
  }

  private BooleanExpression emailContains(String email) {
    return StringUtils.hasText(email) ? user.email.contains(email) : null;
  }
}
