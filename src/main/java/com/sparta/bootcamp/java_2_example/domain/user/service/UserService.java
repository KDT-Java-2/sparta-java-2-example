package com.sparta.bootcamp.java_2_example.domain.user.service;

import com.sparta.bootcamp.java_2_example.common.exception.ServiceException;
import com.sparta.bootcamp.java_2_example.common.exception.ServiceExceptionCode;
import com.sparta.bootcamp.java_2_example.domain.user.dto.UserCreateRequest;
import com.sparta.bootcamp.java_2_example.domain.user.dto.UserResponse;
import com.sparta.bootcamp.java_2_example.domain.user.dto.UserSearchResponse;
import com.sparta.bootcamp.java_2_example.domain.user.dto.UserUpdateRequest;
import com.sparta.bootcamp.java_2_example.domain.user.entity.User;
import com.sparta.bootcamp.java_2_example.domain.user.mapper.UserMapper;
import com.sparta.bootcamp.java_2_example.domain.user.repository.UserQueryRepository;
import com.sparta.bootcamp.java_2_example.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserMapper userMapper;

  private final EntityManager entityManager;
  private final JdbcTemplate jdbcTemplate;

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserQueryRepository userQueryRepository;

  @Transactional
  public Page<UserSearchResponse> searchUser() {
    return null;
  }

  @Transactional(readOnly = true)
  public UserResponse getUserById(Long userId) {
    userRepository.findById(userId)
        .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_USER));
    return null;
  }

  @Transactional
  public void create(UserCreateRequest request) {
    userRepository.save(User.builder()
        .name(request.getName())
        .email(request.getEmail())
        .passwordHash(passwordEncoder.encode(request.getPassword()))
        .build());
  }

  @Transactional
  public void update(Long userId, UserUpdateRequest request) {
    User user = getUser(userId);

    user.setName(request.getName());
    user.setEmail(request.getEmail());

    userRepository.save(user);
  }

  @Transactional
  public void delete(Long userId) {
    userRepository.delete(getUser(userId));
  }

  private User getUser(Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new ServiceException(ServiceExceptionCode.NOT_FOUND_USER));
  }

  @Transactional
  public void saveAllUsers(List<User> users) {
    String sql = "INSERT INTO user (name, email, password_hash, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";

    int[][] result = jdbcTemplate.batchUpdate(sql, users, 1000, (ps, user) -> {
      LocalDateTime now = LocalDateTime.now();
      ps.setString(1, user.getName());
      ps.setString(2, user.getEmail());
      ps.setString(3, user.getPasswordHash());
      ps.setTimestamp(4, Timestamp.valueOf(now));
      ps.setTimestamp(5, Timestamp.valueOf(now));
    });

    // 로깅 참고용
//    AtomicInteger totalProcessed = new AtomicInteger(0);
//    for (int i = 0; i < result.length; i++) {
//      int[] batchResult = result[i];
//      int processedInBatch = Arrays.stream(batchResult).sum();
//      totalProcessed.addAndGet(processedInBatch);
//      log.info("{}번째 배치: {}건 처리 완료.", i + 1, batchResult.length);
//    }
  }

  @Transactional
  public void saveAllUsersWithEntityManager(List<User> users) {
    int batchSize = 1000;
    for (int i = 0; i < users.size(); i++) {
      User user = users.get(i);
      entityManager.persist(user);

      // 1000건마다 DB에 반영하고 메모리를 비운다.
      if ((i + 1) % batchSize == 0) {
        // 1. DB에 쿼리 전송 (데이터 저장)
        entityManager.flush();
        // 2. 영속성 컨텍스트 초기화 (메모리 확보)
        entityManager.clear();
      }
    }
    // 루프 종료 후 남은 데이터 처리
    entityManager.flush();
    entityManager.clear();
  }

}
