package com.study.apps.poc.logging;

import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;

@Component
public class SdkClientSupporter {

    public Region targetRegion() {
        return Region.AP_NORTHEAST_2;
    }

    public AwsCredentialsProvider credentialsProvider() {
//        return ProfileCredentialsProvider.create();
        return ProfileCredentialsProvider.create("sre-user");
    }
}
