dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web:${Versions.springBoot}")
    implementation("org.springframework.boot:spring-boot-starter-security:${Versions.springBoot}")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:${Versions.springBoot}")
    implementation("org.springframework.boot:spring-boot-starter-validation:${Versions.springBoot}")
    implementation("io.swagger.core.v3:swagger-core:${Versions.swagger}")
    implementation("io.jsonwebtoken:jjwt-api:${Versions.jwt}")
    implementation("io.jsonwebtoken:jjwt-impl:${Versions.jwt}")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:${Versions.jwt}")
    implementation("org.postgresql:postgresql:${Versions.postgreSQL}")
    compileOnly("software.amazon.awssdk:aws-sdk-java:${Versions.awsJavaSdk}")
    implementation("software.amazon.awssdk:aws-crt-client:${Versions.awsJavaSdk}")
    implementation("software.amazon.awssdk:apache-client:${Versions.awsJavaSdk}")
}
