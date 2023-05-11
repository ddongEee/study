package com.study.orderapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class OrderApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApiApplication.class, args);
    }

    @RestController
    public static final class HelloController {
        public HelloController(Environment environment) {
            String[] activeProfiles = environment.getActiveProfiles();
            for (String ac : activeProfiles) {
                System.out.println("### + " + ac);
            }
        }
        @GetMapping("/hello")
        public String hello() {
            return "Hello Crayon :) V2";
        }
    }


}
