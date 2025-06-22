package com.sparta.bootcamp.java_2_example.global.config;

import com.sparta.bootcamp.java_2_example.domain.user.service.UserService;
import com.sparta.bootcamp.java_2_example.domain.user.service.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//TODO : 실행 시에는 제거되어야하는 예제입니다.
@Configuration
public class BeanConfig {

  @Bean
  public UserService userService() {
    return new UserServiceImpl();
  }

}
