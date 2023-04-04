package com.study.apps.springjwt;

import java.util.Optional;

public interface UserService {
    Optional<UserDto> login(UserDto userVo);
}
