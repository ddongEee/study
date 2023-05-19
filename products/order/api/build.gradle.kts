dependencies {
    implementation(platform("software.amazon.awssdk:bom:${Versions.awsJavaSdk}"))
    implementation("org.springframework.boot:spring-boot-starter-web:${Versions.springBoot}")
    implementation("org.springframework.boot:spring-boot-starter-security:${Versions.springBoot}")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:${Versions.springBoot}")
    implementation("org.springframework.boot:spring-boot-starter-validation:${Versions.springBoot}")
    implementation("io.swagger.core.v3:swagger-core:${Versions.swagger}")
    implementation("io.jsonwebtoken:jjwt-api:${Versions.jwt}")
    implementation("io.jsonwebtoken:jjwt-impl:${Versions.jwt}")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:${Versions.jwt}")
    implementation("org.postgresql:postgresql:${Versions.postgreSQL}")
//    implementation("software.amazon.awssdk:aws-sdk-java:${Versions.awsJavaSdk}")
    compileOnly("software.amazon.awssdk:aws-sdk-java:${Versions.awsJavaSdk}")
    implementation("software.amazon.awssdk:secretsmanager:${Versions.awsJavaSdk}")
    implementation("software.amazon.awssdk:auth:${Versions.awsJavaSdk}")
    implementation("software.amazon.awssdk:aws-crt-client:${Versions.awsJavaSdk}")
    compileOnly("software.amazon.awssdk:apache-client:${Versions.awsJavaSdk}") //   이슈 : https://github.com/testcontainers/testcontainers-java/issues/1442#issuecomment-694342883

    // for custom logging
    implementation("ch.qos.logback.contrib:logback-json-classic:${Versions.logback}")
    implementation("ch.qos.logback.contrib:logback-jackson:${Versions.logback}")
}
