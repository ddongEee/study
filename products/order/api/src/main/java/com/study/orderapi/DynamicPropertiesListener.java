package com.study.orderapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;

import java.util.Map;

public class DynamicPropertiesListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {
    private static final String orderApiSecretId = "/local/order-api"; // todo : 환경은 빼기
    @SneakyThrows
    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment environment = event.getEnvironment();
        environment.getPropertySources().addFirst(new MapPropertySource("awsSecretManagerProfile", propertyLoader()));
    }

    @SuppressWarnings("unchecked")
    private Map<String,Object> propertyLoader() throws JsonProcessingException {
        SecretsManagerClient secretsManagerClient = SecretsManagerClient.builder()
                .region(Region.AP_NORTHEAST_2)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        ObjectMapper mapper = new ObjectMapper();
        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
                .secretId(orderApiSecretId)
                .build();

        String orderSecrets = secretsManagerClient.getSecretValue(valueRequest).secretString();
        secretsManagerClient.close();
        return mapper.readValue(orderSecrets, Map.class);

    }
}
