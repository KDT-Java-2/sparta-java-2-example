package com.sparta.bootcamp.java_2_example.global.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

  // 1. execution: `controller` 패키지 내의 모든 메서드 실행 전에 적용
  @Before("execution(* com.sparta.bootcamp.java_2_example.domain..controller..*(..))")
  public void logBeforeApiExecution() {
    log.info("[API-execution] API 메서드 실행 전 로그");
  }

  // 2. within: `domain` 패키지 내의 모든 메서드 실행 전에 적용
  @Before("within(com.sparta.bootcamp.java_2_example.domain..*)")
  public void logBeforeWithin() {
    log.info("[within] domain 패키지 내부 메서드 실행 전 로그");
  }

  // 3. @annotation: @Loggable 어노테이션이 붙은 메서드 실행 전에만 적용
  @Before("@annotation(com.sparta.bootcamp.java_2_example.common.annotation.Loggable)")
  public void logBeforeAnnotation() {
    log.info("[@annotation] @Loggable 어노테이션 적용된 메서드 실행 전 로그");
  }

  // 4. JoinPoint 활용: 메서드의 상세 정보 로깅
  @Before("execution(* com.sparta.bootcamp.java_2_example.domain..*(..))")
  public void logMethodDetails(JoinPoint joinPoint) {
    log.info("[JoinPoint 활용] 실행된 메서드 이름: {}", joinPoint.getSignature().getName());
    Object[] args = joinPoint.getArgs();
    if (args.length > 0) {
      log.info("[JoinPoint 활용] 전달된 파라미터: {}", args);
    }
  }

}
