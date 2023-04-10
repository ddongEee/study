package com.study.apps.poc.dlbr;

import com.study.apps.poc.dlbr.service.RedisTester;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {
    private final RedisTester redisTester;
    @GetMapping("/hello")
    public String hello() {
        return Thread.currentThread().getName();
    }

    @GetMapping("/remove-cache")
    public String removeCache() {
        return "done";
    }

}
