dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web:${Versions.springBoot}")
    implementation("org.springframework.boot:spring-boot-starter-data-redis:${Versions.springBoot}")
    implementation("com.amazonaws:aws-java-sdk-sts:${Versions.awsJavaSdkSts}")
    implementation("com.amazonaws:aws-java-sdk-s3:${Versions.awsJavaSdkS3}")
    implementation("dnsjava:dnsjava:${Versions.dnsJava}")
    implementation("org.springframework.boot:spring-boot-configuration-processor:2.7.1")
    implementation("org.springframework.boot:spring-boot-starter-aop:${Versions.springBoot}")
    implementation("io.lettuce:lettuce-core:${Versions.lettuce}")


}
