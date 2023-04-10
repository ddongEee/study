package com.study.apps.springjwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    /**
     * 로그인 구현체
     *
     * @param userDto UserDto
     * @return Optional<UserDto>
     */
    @Override
    public Optional<UserDto> login(UserDto userDto) {
        if (!userDto.getUserId().equals("admin")) {
            return Optional.empty();
        }
        UserDto user = UserDto.builder()
                .userSq(1)
                .userId("admin")
                .userPw("1")
                .userNm("admin")
                .userSt("s")
                .build();
        return Optional.of(user);
    }
}
