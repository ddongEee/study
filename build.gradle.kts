import com.linecorp.support.project.multi.recipe.configureByLabels

plugins {
    id("com.linecorp.build-recipe-plugin") version Versions.lineRecipePlugin
    id("org.springframework.boot") version Versions.springBoot apply false
    id("io.spring.dependency-management") version Versions.springDependencyManagementPlugin apply false
    kotlin("kapt") version Versions.kotlin apply false
    kotlin("jvm") version Versions.kotlin apply false
    kotlin("plugin.jpa") version Versions.kotlin apply false
    kotlin("plugin.spring") version Versions.kotlin apply false
}

allprojects {
    repositories {
        mavenCentral()
//        maven {
//            url = uri("https://maven.restlet.com")
//        }
//        maven {
//            url = uri("https://jitpack.io")
//        }
    }
}

configureByLabels("java") {
    apply(plugin = "java")
    apply(plugin = "org.gradle.java")

    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

configureByLabels("springboot") {
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "org.springframework.boot")
}


configureByLabels("app") {
    apply(plugin = "org.springframework.boot")

    tasks.getByName<Jar>("jar") {
        enabled = false
    }

    tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
        enabled = true
        archiveClassifier.set("boot")
    }
}

configureByLabels("lib") {
    apply(plugin = "java-library")

    tasks.getByName<Jar>("jar") {
        enabled = true
    }
}

configureByLabels("spock") {
    apply(plugin = "groovy")

    dependencies {
        val testImplementation by configurations

        testImplementation("org.apache.groovy:groovy:${Versions.groovy}")
        testImplementation("org.spockframework:spock-core:${Versions.spock}")
    }

    tasks.named<Test>("test") {
        useJUnitPlatform() // Use JUnit Platform for unit tests.
    }
}
