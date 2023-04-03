# Gradle Kotlin
### Task 사용관련
```kotlin
// 새로운 task 생성
tasks.register<Copy>("copyRoot") {
    description = "copy some to classes root"
    dependsOn("classes")
    from("conf") {
        include("*.xml")
    }
    into("$buildDri/classes/java/main")
}

// 기존의 task 수정
tasks.named<Jar>("jar") {
    dependsOn("gitVersion")
    archivename ="${rootProject.name}.jar"
    
    manifest {
        attributes["Main-Class"] = "com.voce.protoss.ProtossMain"
        attributes["Gradle-Version"] = "Gradle " + getProject().getGradle().getGradleVersion()
        attributes["Created-By"] = "Java " + JavaVersion.current()
        attributes["Class-Path"] = configurations.runtimeClasspath.get().filter {
            it.name.endsWith(".jar")
        }.joinToString(separator=" ") { "lib/" + it.name }
    }
}

// java compile시에 인코딩에러
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

// 소스변경이 없더라도(Up-To-Date 상황이더라도) 무조건 실행되게
tasks.register<Copy>("deploy") {
    //    ...
    outputs.upToDateWhen { false }
    //    ...
}
```

## 관련 CLI
```bash
# 모든정보출력
./gradlew build --info
# tasks 확인
./gradlew tasks --all

# task DAG tree 확인위해 plugin 추가 -> id("com.dorongold.task-tree") version "1.4"
./gradlew build taskTree

# 의존성확인
./gradlew dependencies

# 의존성 캐시 지우기 및 새로받기
./gradlew build --refresh-dependencies
```

## Reference
- [Gradle 초보를 위해 핵심만 추렸다](https://ddmix.blogspot.com/2019/10/get-used-to-gradle.html)
