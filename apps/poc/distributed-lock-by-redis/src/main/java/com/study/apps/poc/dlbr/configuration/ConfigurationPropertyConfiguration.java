package com.study.apps.poc.dlbr.configuration;

import com.study.apps.poc.dlbr.service.RedisPropertyHolder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(RedisPropertyHolder.class)
public class ConfigurationPropertyConfiguration {
}
