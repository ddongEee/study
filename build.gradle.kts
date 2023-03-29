import com.linecorp.support.project.multi.recipe.configureByLabels

plugins {
    java
    id("com.linecorp.build-recipe-plugin") version Versions.lineRecipePlugin
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

configureByLabels("spock") {
    apply(plugin = "groovy")

    dependencies {
        testImplementation("org.apache.groovy:groovy:${Versions.groovy}")
        testImplementation("org.spockframework:spock-core:${Versions.spock}")
    }

    tasks.named<Test>("test") {
        useJUnitPlatform() // Use JUnit Platform for unit tests.
    }
}
