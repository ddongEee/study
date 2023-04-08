package com.study.apps.poc.dlbr.support;

import io.lettuce.core.ExpireArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ScheduledTaskAdviceCopy {
    public final AtomicInteger testTryCount = new AtomicInteger(0);
    public final AtomicInteger testReject1Count = new AtomicInteger(0);
    public final AtomicInteger testReject2Count = new AtomicInteger(0);
    public final AtomicInteger testReject3Count = new AtomicInteger(0);
    public final AtomicInteger testReject4Count = new AtomicInteger(0);
    public final AtomicInteger testFailedCount = new AtomicInteger(0);
    public final AtomicInteger testSucceedCount = new AtomicInteger(0);
//    private final RedisTemplate<String, String> redisTemplate;
//    private final StatefulRedisClusterConnection<String, String> statefulRedisClusterConnection;
    private final StatefulRedisConnection<String, String> statefulRedisConnection;

    //    https://redis.io/commands/expireat/ option 중요
    // TTL :  key가 삭제되었으면 -2를 리턴, expire time이 설정되지 않았으면 -1을 리턴한다. (PTTL 은 밀리세컨)
    @Around("@annotation(com.study.apps.poc.dlbr.support.ScheduledTaskAdviceCopy.SynchronousScheduled)")
    public Object proceed(final ProceedingJoinPoint joinPoint) throws Throwable {
        RedisCommands<String, String> sync = statefulRedisConnection.sync();
        testTryCount.incrementAndGet();
        final SynchronousScheduled annotation = getAnnotation(joinPoint.getSignature());
        final String keyName = annotation.name();

        Long foundTtl = sync.pttl(keyName);
        if (foundTtl > 0L) { // incr 은 호출및 expire 설정된 이후
            testReject1Count.incrementAndGet();
            return doNoting();
        }

        if (foundTtl == -1L) { // incr 은 호출되었으나 expire 설정은 아직 안된경우. (타이밍 외에, expire 호출 실패 포함)
            // if the key exists but has no associated expiration time
            Boolean expire = sync.expire(keyName, Duration.ofSeconds(annotation.autoReleaseSeconds()), ExpireArgs.Builder.nx());
            if (expire) {
                testReject2Count.incrementAndGet(); // if the timeout was set.
            } else {
                testReject3Count.incrementAndGet(); // if key does not exist or the timeout could not be se
            }
            return doNoting();
        }

        Long incrementResult = sync.incr(keyName); // lock 잡기 3 1,2,3
        if (incrementResult == null || incrementResult > 1) { // 가장먼저 lock 을 잡지 못한경우
            testReject4Count.incrementAndGet();
            return doNoting();
        }

        sync.expire(keyName, Duration.ofSeconds(annotation.autoReleaseSeconds()));

        try {
            return proceedInternal(joinPoint);
        } finally {
            sync.expire(keyName, Duration.ZERO);
            testSucceedCount.incrementAndGet();
        }
    }

    private Object doNoting() {
        return null;
    }

    private SynchronousScheduled getAnnotation(final Signature signature) {
        if (!(signature instanceof MethodSignature)) {
            return null;
        }

        final Method method = ((MethodSignature)signature).getMethod();
        if (method.getReturnType() != void.class) {
            if (log.isWarnEnabled()) {
                log.warn("SynchronousScheduled method should returns void.");
            }
            return null;
        }
        return method.getAnnotation(SynchronousScheduled.class);
    }

    private Object proceedInternal(final ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (final Throwable e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    public String summeryResultStatus() {
        StringBuilder builder = new StringBuilder(System.getProperty("line.separator")).append("@@ Report");
        builder.append(String.format("testTryCount : %s", testTryCount)).append(System.getProperty("line.separator"));
        builder.append(String.format("testReject1Count : %s", testReject1Count)).append(System.getProperty("line.separator"));
        builder.append(String.format("testReject2Count : %s", testReject2Count)).append(System.getProperty("line.separator"));
        builder.append(String.format("testReject3Count : %s", testReject3Count)).append(System.getProperty("line.separator"));
        builder.append(String.format("testReject4Count : %s", testReject4Count)).append(System.getProperty("line.separator"));
        builder.append(String.format("testFailedCount : %s", testFailedCount)).append(System.getProperty("line.separator"));
        builder.append(String.format("testSucceedCount : %s", testSucceedCount)).append(System.getProperty("line.separator"));
        return builder.toString();
    }

    //@Scheduled
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface SynchronousScheduled {
        long fixedDelay() default -1;
        String name();
        int autoReleaseSeconds() default 2;
    }
}
