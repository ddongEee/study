tasks.withType<Jar> { // Jar 로 task class 지정. 해당 속성 사용가능
    manifest {
        attributes["Main-Class"] = "com.study.sdk4aws.Main"
    }
}
