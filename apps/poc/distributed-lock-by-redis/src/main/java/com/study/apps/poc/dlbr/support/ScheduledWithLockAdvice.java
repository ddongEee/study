package com.study.apps.poc.dlbr.support;

import io.lettuce.core.ExpireArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.Duration;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
@SuppressWarnings("DuplicatedCode")
public class ScheduledWithLockAdvice {
    private final StatefulRedisConnection<String, String> statefulRedisConnection;

    @Around("@annotation(com.study.apps.poc.dlbr.support.ScheduledWithLockAdvice.ScheduledWithLock)")
    public Object proceed(final ProceedingJoinPoint joinPoint) throws Throwable {
        RedisCommands<String, String> sync = statefulRedisConnection.sync();
        final ScheduledWithLock annotation = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(ScheduledWithLock.class);
        final String keyName = annotation.name();

        Long foundTtl = sync.pttl(keyName);
        if (foundTtl > 0L) { // incr 은 호출및 expire 설정된 이후
            return doNoting();
        }

        if (foundTtl == -1L) { // incr 은 호출되었으나 expire 설정은 아직 안된경우. (타이밍 외에, expire 호출 실패 포함)
            // if the key exists but has no associated expiration time
            sync.expire(keyName, Duration.ofSeconds(annotation.autoReleaseSeconds()), ExpireArgs.Builder.nx());
            return doNoting();
        }

        Long incrementResult = sync.incr(keyName); // lock 잡기 3 1,2,3
        if (incrementResult == null || incrementResult > 1) { // 가장먼저 lock 을 잡지 못한경우
            return doNoting();
        }

        sync.expire(keyName, Duration.ofSeconds(annotation.autoReleaseSeconds()));

        try {
            return joinPoint.proceed();
        } catch (final Throwable e) {
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            sync.expire(keyName, Duration.ZERO);
        }
    }

    private Object doNoting() {
        return null;
    }

    @Scheduled
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ScheduledWithLock {
        long fixedDelay() default -1;
        String name() default "Sample";
        int autoReleaseSeconds() default 2;
    }
}
