package com.study.orderapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class OrderApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApiApplication.class, args);
    }

    @RestController
    public static final class HelloController {
        @GetMapping("/hello")
        public String hello() {
            return "Hello Crayon :)";
        }
    }


}
