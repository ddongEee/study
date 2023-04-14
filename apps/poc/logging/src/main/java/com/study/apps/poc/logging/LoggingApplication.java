package com.study.apps.poc.logging;

import com.study.apps.poc.logging.service.CloudWatchDirector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@SpringBootApplication
public class LoggingApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(LoggingApplication.class);
        application.run(args);
    }

    @Slf4j
    @RestController
    @RequiredArgsConstructor
    public static class TestRunner implements ApplicationRunner {
        private final CloudWatchDirector cloudWatchDirector;

        @Override
        public void run(ApplicationArguments args) throws InterruptedException {
//            testMDC();
//            sendMetric();
        }

        private void sendMetric() throws InterruptedException {
            // 2023-04-11 14:26:02 시작
            for (int i = 0; i<100000; i++) {
                cloudWatchDirector.putMetricData(Double.parseDouble(String.valueOf(i)));
                log.info("send");
                Thread.sleep(1000);
            }
        }

        private void testMDC() {
            MDC.put("userId", "crayon");
            MDC.put("event", "orderProduct");
            MDC.put("transactionId", "a123");
            log.error("mdc test");
            MDC.clear();
            log.warn("after mdc.clear");
        }
    }
}
