package com.study.apps.poc.dlbr.service;

import io.lettuce.core.RedisClient;
import io.lettuce.core.event.EventBus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisEventHandler {
//    private final RedisTemplate<String,String> redisTemplate;
    private final RedisConnectionFactory redisConnectionFactory;

    @Scheduled(fixedDelay = 1000)
    public void test() {
//        log.info("redisConnectionFactory.getConnection() : {}", redisConnectionFactory.getConnection().getNativeConnection());
//        log.info("redisConnectionFactory.getClusterConnection() : {}", redisConnectionFactory.getClusterConnection());
//        log.info("redisConnectionFactory.getSentinelConnection() : {}", redisConnectionFactory.getSentinelConnection());
    }

}
