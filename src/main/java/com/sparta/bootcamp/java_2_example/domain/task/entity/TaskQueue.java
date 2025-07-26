package com.sparta.bootcamp.java_2_example.domain.task.entity;

import com.sparta.bootcamp.java_2_example.common.enums.TaskStatus;
import com.sparta.bootcamp.java_2_example.common.enums.TaskType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@Table(name = "task_queue")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskQueue {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Setter
  @Column
  Long eventId;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  TaskType taskType;

  @Setter
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  TaskStatus status;

  @Column(name = "created_at", nullable = false, updatable = false)
  @CreationTimestamp
  LocalDateTime createdAt;

  @Column(name = "updated_at")
  @UpdateTimestamp
  LocalDateTime updatedAt;

  @Builder
  public TaskQueue(TaskType taskType, TaskStatus status) {
    this.taskType = taskType;
    this.status = status;
  }
}