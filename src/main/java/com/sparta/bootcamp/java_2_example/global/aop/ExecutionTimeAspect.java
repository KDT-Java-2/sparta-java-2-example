package com.sparta.bootcamp.java_2_example.global.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ExecutionTimeAspect {

  // Pointcut: Service 계층의 모든 메서드를 대상으로 지정
  @Pointcut("execution(* com.sparta.bootcamp.java_2_example.domain..service..*(..))")
  private void allServiceMethods() {
  }

  @Around("allServiceMethods()")
  public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
    // 1. 메서드 실행 전: 시작 시간 기록
    long startTime = System.currentTimeMillis();

    // 2. 실제 타겟 메서드 실행
    Object result = joinPoint.proceed();

    // 3. 메서드 실행 후: 종료 시간 기록 및 실행 시간 계산/로깅
    long endTime = System.currentTimeMillis();
    long executionTime = endTime - startTime;
    log.info("'{}' 메서드 실행 시간: {}ms", joinPoint.getSignature().toShortString(), executionTime);

    // 4. 원래 메서드의 실행 결과를 반환
    return result;
  }

}
