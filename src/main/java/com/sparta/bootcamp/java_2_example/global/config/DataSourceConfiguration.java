package com.sparta.bootcamp.java_2_example.global.config;

import com.zaxxer.hikari.HikariDataSource;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class DataSourceConfiguration {

  public static final String MASTER_DATASOURCE = "masterDataSource";
  public static final String SLAVE_DATASOURCE = "slaveDataSource";

  // Master 데이터 소스 빈 생성 (쓰기 전용)
  @Bean(MASTER_DATASOURCE)
  @ConfigurationProperties(prefix = "spring.datasource.master.hikari")
  public DataSource masterDataSource() {
    return DataSourceBuilder.create()
        .type(HikariDataSource.class)
        .build();
  }

  // Slave 데이터 소스 빈 생성 (읽기 전용)
  @Bean(SLAVE_DATASOURCE)
  @ConfigurationProperties(prefix = "spring.datasource.slave.hikari")
  public DataSource slaveDataSource() {
    return DataSourceBuilder.create()
        .type(HikariDataSource.class)
        .build();
  }

  // 동적 라우팅 데이터 소스 빈 생성
  @Bean
  @Primary
  @DependsOn({MASTER_DATASOURCE, SLAVE_DATASOURCE})
  public DataSource routingDataSource(
      @Qualifier(MASTER_DATASOURCE) DataSource masterDataSource,
      @Qualifier(SLAVE_DATASOURCE) DataSource slaveDataSource) {

    // Custom RoutingDataSource 인스턴스 생성
    RoutingDataSource routingDataSource = new RoutingDataSource();

    // 타깃 데이터소스 매핑: 키("master", "slave")는 determineCurrentLookupKey() 메서드와 일치해야 함
    Map<Object, Object> datasourceMap = new HashMap<>();
    datasourceMap.put("master", masterDataSource);
    datasourceMap.put("slave", slaveDataSource);

    routingDataSource.setTargetDataSources(datasourceMap);
    // 기본 데이터 소스로 master를 지정 (트랜잭션이 읽기 전용이 아닐 경우 사용)
    routingDataSource.setDefaultTargetDataSource(masterDataSource);
    routingDataSource.afterPropertiesSet(); // 내부 설정값 초기화

    return routingDataSource;
  }

  @Bean
  public DataSourceRoutingAspect dataSourceRoutingAspect() {
    return new DataSourceRoutingAspect();
  }

  @Aspect
  @Component
  @Slf4j
  @Order(1)
  public static class DataSourceRoutingAspect {

    @Before("@annotation(org.springframework.transaction.annotation.Transactional)")
    public void setDataSourceKey(JoinPoint joinPoint) {
      MethodSignature signature = (MethodSignature) joinPoint.getSignature();
      Method method = signature.getMethod();

      Transactional transactional = method.getAnnotation(Transactional.class);
      if (transactional != null && transactional.readOnly()) {
        DataSourceContextHolder.setDataSourceKey("slave");
        log.info("Set datasource to slave for readOnly transaction");
      } else {
        DataSourceContextHolder.setDataSourceKey("master");
        log.info("Set datasource to master for write transaction");
      }
    }

    @After("@annotation(org.springframework.transaction.annotation.Transactional)")
    public void clearDataSourceKey() {
      DataSourceContextHolder.clearDataSourceKey();
    }
  }
}