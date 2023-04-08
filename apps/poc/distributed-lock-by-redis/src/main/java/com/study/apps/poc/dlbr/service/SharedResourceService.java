package com.study.apps.poc.dlbr.service;

import com.study.apps.poc.dlbr.support.DistributedLockAdvice;
import com.study.apps.poc.dlbr.support.ScheduledTaskAdvice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@RequiredArgsConstructor
@SuppressWarnings("DuplicatedCode")
public class SharedResourceService {
    private static final String NON_LOCK_KEY = "NonLock";
    private static final String SINGLETON_SCHEDULE_LOCK_KEY = "SingletonScheduleLock";
    private static final String DISTRIBUTE_LOCK_KEY = "DistributeLock";
    private final AtomicInteger nonLockMethodRunCount = new AtomicInteger(0);
    private final AtomicInteger singletonScheduleLockMethodRunCount = new AtomicInteger(0);
    private final AtomicInteger distributeLockMethodRunCount = new AtomicInteger(0);
    private final RedisTemplate<String, String> redisTemplate;

    public void clean() {
        redisTemplate.delete(NON_LOCK_KEY);
        redisTemplate.delete(SINGLETON_SCHEDULE_LOCK_KEY);
        redisTemplate.delete(DISTRIBUTE_LOCK_KEY);
    }

    public void incrValueWithNonLock() {
        criticalSectionMethod(NON_LOCK_KEY, nonLockMethodRunCount);
    }

    @ScheduledTaskAdvice.SynchronousScheduled(name = "singletonScheduleLock", fixedDelay = 1000)
    public void incrValueWithSingletonScheduleLock() {
        criticalSectionMethod(SINGLETON_SCHEDULE_LOCK_KEY, singletonScheduleLockMethodRunCount);
    }

    @DistributedLockAdvice.DistributedLock(name = "distributedLock", autoReleaseSeconds = 3)
    public void incrValueWithDistributeLock() {
        criticalSectionMethod(DISTRIBUTE_LOCK_KEY, distributeLockMethodRunCount);
    }

    private void criticalSectionMethod(final String key, final AtomicInteger count) {
        count.incrementAndGet();
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        Integer targetNumber = Optional.ofNullable(operations.get(key))
                .map(Integer::parseInt)
                .orElse(0);
        operations.set(key, String.valueOf(targetNumber+1));
        redisTemplate.expire(key, Duration.ofSeconds(60));
    }

    public void printResult() {
        String report = System.getProperty("line.separator") + "@@ Report" + System.getProperty("line.separator") +
                String.format("1-1. nonLockMethodRunCount : %s", this.nonLockMethodRunCount) + System.getProperty("line.separator") +
                String.format("1-2. nonLockRemoteCount : %s", getRemoteCount(NON_LOCK_KEY)) + System.getProperty("line.separator") +
                String.format("2-1. singletonScheduleLockMethodRunCount : %s", this.singletonScheduleLockMethodRunCount) + System.getProperty("line.separator") +
                String.format("2-2. singletonScheduleLockRemoteCount : %s", getRemoteCount(SINGLETON_SCHEDULE_LOCK_KEY)) + System.getProperty("line.separator") +
                String.format("3-1. distributeLockMethodRunCount : %s", this.distributeLockMethodRunCount) + System.getProperty("line.separator") +
                String.format("3-2. distributeLockRemoteCount : %s", getRemoteCount(DISTRIBUTE_LOCK_KEY)) + System.getProperty("line.separator");
        log.info(report);
    }

    private Integer getRemoteCount(final String key) {
        String result = redisTemplate.opsForValue().get(key);
        return Integer.parseInt(Objects.requireNonNull(result));
    }
}
