dependencies {
    implementation(project(":libs:opencsv"))
    implementation("com.opencsv:opencsv:${Versions.opencsv}")
    implementation("com.fasterxml.jackson.core:jackson-databind:${Versions.jackson}")

    implementation("org.springframework.boot:spring-boot-starter-validation:${Versions.springBoot}")
    implementation("org.springframework.boot:spring-boot-starter-web:${Versions.springBoot}")
    implementation("org.springframework.boot:spring-boot-starter-actuator:${Versions.springBoot}")
    implementation("org.springframework.data:spring-data-commons:${Versions.springData}")
    implementation("org.flywaydb:flyway-core:${Versions.flyway}")
    testImplementation("org.springframework.boot:spring-boot-starter-test:${Versions.springBoot}")
}
