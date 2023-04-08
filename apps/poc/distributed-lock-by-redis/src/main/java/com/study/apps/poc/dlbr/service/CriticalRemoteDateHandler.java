package com.study.apps.poc.dlbr.service;

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.springframework.stereotype.Component;

@Component
public class CriticalRemoteDateHandler {
    private static final String KEY = "key";
    private final RedisCommands<String , String> redisCommands;

    public CriticalRemoteDateHandler(final StatefulRedisConnection<String, String> statefulRedisConnection) {
        this.redisCommands = statefulRedisConnection.sync();
    }

    public void init() {
        redisCommands.set(KEY,String.valueOf(0));
    }

    public void plusCount() {
        int resolvedCount = Integer.parseInt(redisCommands.get(KEY));
        redisCommands.set(KEY, String.valueOf(resolvedCount+1));
    }

    public int getCount() {
        return Integer.parseInt(redisCommands.get(KEY));
    }
}
