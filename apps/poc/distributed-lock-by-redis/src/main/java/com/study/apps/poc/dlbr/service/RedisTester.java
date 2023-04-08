package com.study.apps.poc.dlbr.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisTester {
    private final RedisTemplate<String, String> redisTemplate;

    public String setAndGet(final String key, final String value) {
        redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(2));
        return redisTemplate.opsForValue().get(key);
    }

    public void testSet(final String key, final String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void bulkTest() {

    }
}
