package com.study.apps.poc.dlbr.configuration;

import com.study.apps.poc.dlbr.service.RedisPropertyHolder;
import io.lettuce.core.*;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CacheConfiguration {
    private final RedisPropertyHolder redisPropertyHolder;

//    @Bean
//    @Profile("prod")
//    public RedisConnectionFactory redisConnectionFactory() {
//        RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration();
//        clusterConfiguration.clusterNode(redisPropertyHolder.getHost(), redisPropertyHolder.getPort());
//
//        ClientOptions clientOptions = ClientOptions.builder()
//                .socketOptions(
//                        SocketOptions.builder()
//                                .connectTimeout(Duration.ofMillis(redisPropertyHolder.getTimeout()))
//                                .keepAlive(true)
//                                .build()
//                )
//                .build();
//
//        LettuceClientConfiguration clientConfiguration = LettuceClientConfiguration.builder()
//                .clientOptions(clientOptions)
//                .commandTimeout(Duration.ofSeconds(redisPropertyHolder.getTimeout()))
//                .build();
//        return new LettuceConnectionFactory(clusterConfiguration, clientConfiguration);
//    }

    /* Default 설정관련 : RedisURI 클래스 참고*/
    @Bean
    @Profile("prod")
    public RedisConnectionFactory redisConnectionFactory() {
        // Default value 는 ClusterTopologyRefreshOptions 에 선언되어있음
        ClusterTopologyRefreshOptions topologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
                .dynamicRefreshSources(true)
                .enablePeriodicRefresh(redisPropertyHolder.enablePeriodicRefreshDuration())
                .enableAllAdaptiveRefreshTriggers()
                .adaptiveRefreshTriggersTimeout(redisPropertyHolder.adaptiveRefreshTriggersTimeoutDuration())
                .build();

        ClusterClientOptions clientOptions = ClusterClientOptions.builder()
                .autoReconnect(true) // 노상관. default true임
                .disconnectedBehavior(ClientOptions.DisconnectedBehavior.DEFAULT) // 노상관. default DEFAULT임
                .socketOptions(SocketOptions.builder()
                                            .connectTimeout(redisPropertyHolder.connectTimeoutDuration())
                                            .keepAlive(true)
                                            .build())
                .publishOnScheduler(true) // 파악필요. default false임. 근데 상관없을듯
                .timeoutOptions(TimeoutOptions.enabled(redisPropertyHolder.timeoutDuration())) // default 모두 false . 체크하기
                .topologyRefreshOptions(topologyRefreshOptions) //
                .build();

//        DefaultClientResources clientResources = DefaultClientResources.builder()
//                .build();
//        clientResources.eventBus().get().subscribe(event -> log.info("@@@@@ lettuce event : " + event));

        // LettuceClientConfigurationBuilder 밑에 default 설정잇음
        LettuceClientConfiguration clientConfiguration = LettuceClientConfiguration.builder()
                .commandTimeout(redisPropertyHolder.timeoutDuration()) // Default : 60 sec
                .readFrom(ReadFrom.MASTER_PREFERRED) // ReadFrom.REPLICA_PREFERRED
                .shutdownTimeout(redisPropertyHolder.shutdownTimeoutDuration()) // 100 milli
                .clientOptions(clientOptions)
//                .clientResources(clientResources)
                .build();

        RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration();
        clusterConfiguration.clusterNode(redisPropertyHolder.getHost(), redisPropertyHolder.getPort());

        return new LettuceConnectionFactory(clusterConfiguration, clientConfiguration);
    }

    @Bean
    public StatefulRedisConnection<String, String> statefulRedisConnection() {
        // Syntax: redis://[password@]host[:port]
        // Syntax: redis://[username:password@]host[:port]
        String connectInfo = String.format("redis://@%s:%s", redisPropertyHolder.getHost(), redisPropertyHolder.getPort());
        System.out.println("@@@@@@@ : " + connectInfo);
//        RedisClusterClient redisClient = RedisClusterClient.create(connectInfo);
        RedisClient redisClient = RedisClient.create(connectInfo);
        return redisClient.connect();
//        System.out.println("Connected to Redis");
//        connection.close();
//        redisClient.shutdown();
    }


    @Bean
    @Profile("prod")
    public RedisTemplate<String, String> redisTemplate() {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }
}
