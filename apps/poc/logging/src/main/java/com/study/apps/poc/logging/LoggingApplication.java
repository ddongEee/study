package com.study.apps.poc.logging;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class LoggingApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(LoggingApplication.class);
        application.setWebApplicationType(WebApplicationType.NONE);
        application.run(args);
    }

    @Slf4j
    @Component
    public static class TestRunner implements ApplicationRunner {

        @Override
        public void run(ApplicationArguments args) {
            MDC.put("userId", "crayon");
            MDC.put("event", "orderProduct");
            MDC.put("transactionId", "a123");
            log.error("mdc test");
            MDC.clear();
            log.warn("after mdc.clear");
        }
    }
}
