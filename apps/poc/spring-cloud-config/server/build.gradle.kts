dependencies {
    implementation("com.amazonaws:aws-java-sdk-ssm:${Versions.awsJavaSdkSsm}")
    implementation("com.amazonaws:aws-java-sdk-secretsmanager:${Versions.awsJavaSdkSecretsManager}")
    implementation("org.springframework.cloud:spring-cloud-config-server:${Versions.springCloud}")
}
