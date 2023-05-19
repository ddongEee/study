package com.study.orderapi;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@SpringBootApplication
public class OrderApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApiApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void run() {
        // none
    }

    @RestController
    public static final class HelloController {
        public HelloController(Environment environment) {
            String[] activeProfiles = environment.getActiveProfiles();
            for (String ac : activeProfiles) {
                log.info("### + " + ac);
            }
        }

        @GetMapping("/hello")
        public String hello() {
            return "Hello Crayon :) V8";
        }

        @GetMapping("/api/hello")
        public String helloApi() {
            return "Hello Crayon API ;) V3";
        }
    }
}
