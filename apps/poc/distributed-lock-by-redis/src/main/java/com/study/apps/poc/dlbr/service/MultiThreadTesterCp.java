package com.study.apps.poc.dlbr.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import static com.study.apps.poc.dlbr.support.ScheduledWithLockAdvice.ScheduledWithLock;

@Slf4j
@Component
@RequiredArgsConstructor
public class MultiThreadTesterCp {
    private static final int THREAD_COUNT = 10;
    private static final int TASK_COUNT_PER_TEST = 1000;
    private final SharedResourceService sharedResourceService;

    public void test() {
        this.sharedResourceService.clean();

        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(THREAD_COUNT);
        for (int i=0; i< TASK_COUNT_PER_TEST; i++) {
            threadPoolExecutor.submit(this.sharedResourceService::incrValueWithNonLock);
        }

        for (int i=0; i< TASK_COUNT_PER_TEST; i++) {
            threadPoolExecutor.submit(this.sharedResourceService::incrValueWithSingletonScheduleLock);
        }

        for (int i=0; i< TASK_COUNT_PER_TEST; i++) {
            threadPoolExecutor.submit(this.sharedResourceService::incrValueWithDistributeLock);
        }

        threadPoolExecutor.submit(() -> {
            Thread.sleep(5000);
            this.sharedResourceService.printResult();
            return null;
        });
    }
}
