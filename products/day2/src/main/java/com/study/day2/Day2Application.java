package com.study.day2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class Day2Application {
    public static void main(String[] args) {
        SpringApplication.run(Day2Application.class);
    }

    @RestController
    public static final class HelloController {
        @GetMapping("/hello")
        public String hello() {
            return "Hello Crayon :)";
        }
    }
}
