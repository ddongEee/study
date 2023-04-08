dependencies {
    implementation("javax.xml.bind:jaxb-api:${Versions.jaxb}")
    implementation("com.amazonaws:aws-java-sdk-kms:${Versions.awsJavaSdkKms}")
    implementation("org.springframework.boot:spring-boot-starter:${Versions.springBoot}")
    implementation("org.springframework.boot:spring-boot-starter-web:${Versions.springBoot}")
    implementation("org.springframework.boot:spring-boot-starter-actuator:${Versions.springBoot}")
    implementation("org.springframework.cloud:spring-cloud-starter-config:${Versions.springCloud}")
    implementation("org.springframework.cloud:spring-cloud-starter-bootstrap:${Versions.springCloud}")
}
