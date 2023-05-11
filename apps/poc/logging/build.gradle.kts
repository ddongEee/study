dependencies {
    implementation(platform("software.amazon.awssdk:bom:${Versions.awsJavaSdk}"))
    implementation("software.amazon.awssdk:cloudwatch:${Versions.awsJavaSdk}")
    implementation("software.amazon.awssdk:cloudwatchlogs:${Versions.awsJavaSdk}")
    implementation("org.springframework.boot:spring-boot-starter-web:${Versions.springBoot}")

    implementation("software.amazon.awssdk:sts:${Versions.awsJavaSdk}")
}
