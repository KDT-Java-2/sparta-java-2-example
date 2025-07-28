package com.sparta.bootcamp.java_2_example.domain.user.mapper;

import com.sparta.bootcamp.java_2_example.domain.user.dto.UserCreateResponse;
import com.sparta.bootcamp.java_2_example.domain.user.dto.UserRequest;
import com.sparta.bootcamp.java_2_example.domain.user.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

  User toUser(UserRequest request);

  UserCreateResponse toCreateResponse(User user);

}
