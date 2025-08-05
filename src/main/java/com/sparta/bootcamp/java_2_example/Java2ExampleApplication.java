package com.sparta.bootcamp.java_2_example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableRetry
@EnableAsync
@EnableScheduling
@EnableFeignClients
@SpringBootApplication
public class Java2ExampleApplication {

  public static void main(String[] args) {
    SpringApplication.run(Java2ExampleApplication.class, args);
  }

}
