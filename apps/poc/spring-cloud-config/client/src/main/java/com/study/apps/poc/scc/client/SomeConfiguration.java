package com.study.apps.poc.scc.client;

import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class SomeConfiguration {
    private final ConfigServiceV3 configService;

    public SomeConfiguration(final ConfigServiceV3 configService) {
        this.configService= configService;
    }

    @Bean
    public String dataSourceA() {
        return "dataSourceAAAAAA";
    }

    @Bean
    public String dataSourceB() {
        return "dataSourceBBBBBB";
    }

    @Component
    @RefreshScope
    public static class DataSourceProxy {
        private final ConfigServiceV3 configServiceV3;
        private final String dataSourceA;
        private final String dataSourceB;

        public DataSourceProxy(final ConfigServiceV3 configServiceV3,
                               final String dataSourceA,
                               final String dataSourceB) {
            this.configServiceV3 = configServiceV3;
            this.dataSourceA = dataSourceA;
            this.dataSourceB = dataSourceB;
        }

        public String activeDataSource() {
            return configServiceV3.isTargetDatasourceA() ? dataSourceA : dataSourceB;
        }
    }
}
