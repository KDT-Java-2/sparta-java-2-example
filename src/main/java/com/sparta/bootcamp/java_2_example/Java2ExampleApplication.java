package com.sparta.bootcamp.java_2_example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableAsync
@EnableRedisHttpSession
@SpringBootApplication
public class Java2ExampleApplication {

  public static void main(String[] args) {
    SpringApplication.run(Java2ExampleApplication.class, args);
  }

}
