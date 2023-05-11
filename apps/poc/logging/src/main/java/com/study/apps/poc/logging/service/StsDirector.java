package com.study.apps.poc.logging.service;

import com.study.apps.poc.logging.SdkClientSupporter;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.GetCallerIdentityResponse;

@Slf4j
@Component
public class StsDirector {
    private final StsClient client;

    public StsDirector(final SdkClientSupporter supporter) {
        this.client = StsClient.builder()
                .region(supporter.targetRegion())
                .credentialsProvider(supporter.credentialsProvider())
                .build();
    }

    public Identity printGetCallerIdentity() {
        log.info("### Current local profile env : {}", System.getenv("AWS_PROFILE"));
        GetCallerIdentityResponse callerIdentity = client.getCallerIdentity();
        return Identity.builder()
                .userId(callerIdentity.userId())
                .account(callerIdentity.account())
                .arn(callerIdentity.arn())
                .build();
    }

    @Getter
    @Builder
    public static final class Identity {
        private final String userId;
        private final String account;
        private final String arn;
    }
}
