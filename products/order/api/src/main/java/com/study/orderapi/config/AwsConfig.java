package com.study.orderapi.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsConfig {
//    @Bean
//    public AWSSecretsManager awsSecretsManager() {
//        return AWSSecretsManagerClientBuilder.standard()
//                .withRegion(Regions.AP_NORTHEAST_2)
//                .withCredentials(ProfileCredentialsProvider.create())
//                .build();
//    }

//    @Bean
//    public String createSecret(String secretName, String secretValue) {
//        CreateSecretRequest request = new CreateSecretRequest()
//                .withName(secretName)
//                .withSecretString(secretValue);
//
//        CreateSecretResult result = awsSecretsManager.createSecret(request);
//
//        String arn = result.getARN();
//        System.out.println("Created secret with ARN: " + arn);
//    }
}
