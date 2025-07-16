package com.sparta.bootcamp.java_2_example.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.sparta.bootcamp.java_2_example.domain.user.entity.User;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class UserServiceTest {

  @Autowired
  private UserService userService;

  @Autowired
  private EntityManager entityManager;

  @Test
  void saveAllUsers() {
    // Given
    List<User> users = createTestUsers(2500); // 배치 크기(1000)보다 큰 수로 테스트

    // When
    userService.saveAllUsers(users);

    // Then
    // 실제 데이터가 올바르게 저장되었는지 확인
    List<User> savedUsers = entityManager.createQuery(
        "SELECT u FROM User u ORDER BY u.id", User.class
    ).getResultList();

    assertThat(savedUsers).hasSize(2501);
  }

  private List<User> createTestUsers(int count) {
    List<User> users = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      users.add(User.builder()
          .name("User" + i)
          .email("user" + i + "@example.com")
          .passwordHash("hash" + i)
          .build());
    }
    return users;
  }
}