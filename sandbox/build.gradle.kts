plugins {
    // Apply the groovy plugin to also add support for Groovy (needed for Spock)
    groovy
    // Apply the java-library plugin for API and implementation separation.
    `java-library`
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    implementation(project(":libs:opencsv"))
    implementation("com.opencsv:opencsv:${Versions.opencsv}")
    implementation("com.fasterxml.jackson.core:jackson-databind:${Versions.jackson}")
    testImplementation("org.apache.groovy:groovy:${Versions.groovy}")
    testImplementation("org.spockframework:spock-core:${Versions.spock}")
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}
