package com.sparta.bootcamp.java_2_example.domain.user.repository;

import com.sparta.bootcamp.java_2_example.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}
