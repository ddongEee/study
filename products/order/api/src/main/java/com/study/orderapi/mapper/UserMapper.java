package com.study.orderapi.mapper;

import com.study.orderapi.model.User;
import com.study.orderapi.rest.dto.UserDto;

public interface UserMapper {

    UserDto toUserDto(User user);
}
