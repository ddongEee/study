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

include("apps:poc:distributed-lock-by-redis")
include("apps:poc:hellots")
include("apps:poc:jambda")
include("apps:poc:logging")
include("apps:poc:lottery")
include("apps:poc:sdk4aws")
include("apps:poc:spring-cloud-config:client")
include("apps:poc:spring-cloud-config:server")
include("apps:poc:springjwt")
include("apps:poc:stock")

include("learn:coding-test")

include("libs:jackson")
include("libs:opencsv")
