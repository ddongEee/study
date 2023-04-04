package com.study.apps.springjwt;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/v1/test")
public class TestController {

    /**
     * [API] 사용자 정보를 기반으로 JWT를 발급하는 API
     * @param userDto UserDto
     * @return ApiResponseWrapper<ApiResponse> : 응답 결과 및 응답 코드 반환
     */
    @PostMapping("/generateToken")
//    @Operation(summary = "토큰 발급", description = "사용자 정보를 기반으로 JWT를 발급하는 API")
    public ResponseEntity<ApiResponse> selectCodeList(@RequestBody UserDto userDto) {

        String resultToken = TokenUtils.generateJwtToken(userDto);

        ApiResponse ar = ApiResponse.builder()
                // BEARER {토큰} 형태로 반환을 해줍니다.
                .result(AuthConstants.TOKEN_TYPE + " " + resultToken)
                .resultCode("200")
                .resultMsg("SUCCESS")
                .build();

        return new ResponseEntity<>(ar, HttpStatus.OK);
    }

    @Builder
    @Getter
    public static final class ApiResponse {
        private final String result;
        private final String resultCode;
        private final String resultMsg;
    }
}
