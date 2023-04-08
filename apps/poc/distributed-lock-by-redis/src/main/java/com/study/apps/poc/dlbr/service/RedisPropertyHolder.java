package com.study.apps.poc.dlbr.service;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Data
@ToString
@ConfigurationProperties("spring.redis")
public class RedisPropertyHolder {
    private String host;
    private Integer port;
    private long timeout;
    private long enablePeriodicRefresh;
    private long adaptiveRefreshTriggersTimeout;
    private long shutdownTimeout;
    private long connectTimeout;

    public Duration timeoutDuration() {
        return Duration.ofMillis(this.timeout);
    }

    public Duration enablePeriodicRefreshDuration() {
        return Duration.ofMillis(this.enablePeriodicRefresh);
    }

    public Duration adaptiveRefreshTriggersTimeoutDuration() {
        return Duration.ofMillis(this.adaptiveRefreshTriggersTimeout);
    }

    public Duration shutdownTimeoutDuration() {
        return Duration.ofMillis(this.shutdownTimeout);
    }

    public Duration connectTimeoutDuration() {
        return Duration.ofMillis(this.connectTimeout);
    }
}
