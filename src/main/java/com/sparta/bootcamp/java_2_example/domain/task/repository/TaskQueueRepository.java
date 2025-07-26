package com.sparta.bootcamp.java_2_example.domain.task.repository;

import com.sparta.bootcamp.java_2_example.common.enums.TaskStatus;
import com.sparta.bootcamp.java_2_example.domain.task.entity.TaskQueue;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskQueueRepository extends JpaRepository<TaskQueue, Long> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT tq FROM TaskQueue tq WHERE tq.id = :id")
  Optional<TaskQueue> findByIdForUpdate(@Param("id") Long id);

  List<TaskQueue> findAllByStatus(TaskStatus taskStatus);
}
