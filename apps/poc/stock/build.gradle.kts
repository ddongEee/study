dependencies {
    implementation(project(":libs:opencsv"))
    implementation("com.opencsv:opencsv:${Versions.opencsv}")
    implementation("com.fasterxml.jackson.core:jackson-databind:${Versions.jackson}")

    implementation("org.springframework.boot:spring-boot-starter-web:${Versions.springBoot}")
    implementation("org.springframework.boot:spring-boot-starter:${Versions.springBoot}")
    implementation("com.google.guava:guava:${Versions.guava}")
    implementation("org.jsoup:jsoup:${Versions.jsoup}")
}
