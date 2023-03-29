dependencies {
    implementation(project(":libs:opencsv"))
    implementation("com.opencsv:opencsv:${Versions.opencsv}")
    implementation("com.fasterxml.jackson.core:jackson-databind:${Versions.jackson}")

    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.data:spring-data-commons")
    implementation("org.flywaydb:flyway-core")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
