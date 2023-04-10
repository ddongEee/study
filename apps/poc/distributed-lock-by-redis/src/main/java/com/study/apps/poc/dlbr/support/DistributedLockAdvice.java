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
public class DistributedLockAdvice {
    private final StatefulRedisConnection<String, String> statefulRedisConnection;

    @Around("@annotation(com.study.apps.poc.dlbr.support.DistributedLockAdvice.DistributedLock)")
    public Object proceed(final ProceedingJoinPoint joinPoint) throws Throwable {
        RedisCommands<String, String> sync = statefulRedisConnection.sync();
        DistributedLock annotation = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(DistributedLock.class);
        final String keyName = annotation.name();

        // spin lock
        while (true) {
            Long foundTtl = sync.pttl(keyName);
            if (foundTtl > 0L) {
                continue;
            }

            if (foundTtl == -1L) {
                sync.expire(keyName, Duration.ofSeconds(annotation.autoReleaseSeconds()), ExpireArgs.Builder.nx());
                continue;
            }

            Long incrementResult = sync.incr(keyName);
            if (incrementResult == null || incrementResult > 1) {
                continue;
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
    }

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface DistributedLock {
        String name();
        int autoReleaseSeconds() default 2;
    }
}
