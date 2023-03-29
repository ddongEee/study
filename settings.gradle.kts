pluginManagement {
    repositories {
        gradlePluginPortal() // build-recipe-plugin 관련 repo
        mavenCentral() // 일반 plugin
//        maven {
//            url = uri("https://maven.springframework.org/release")
//        }
//        maven {
//            url = uri("https://maven.restlet.com")
//        }
    }
}
rootProject.name = "study"
include("libs:jackson")
include("libs:opencsv")
include("apps:lottery-api")
include("apps:sandbox")
include("apps:hellots")
