dependencies {
    implementation("com.amazonaws:aws-lambda-java-core:1.2.1")
    implementation("com.amazonaws:aws-lambda-java-events:3.11.0")
    implementation("com.google.code.gson:gson:2.8.9")
    runtimeOnly("com.amazonaws:aws-lambda-java-log4j2:1.5.0")
}

tasks.register<Zip>("buildZip") {
    dependsOn("build")
    archiveFileName.set("jambda.zip")
//    from compileJava
//    from processResources
//    into('lib') {
//        from configurations.runtimeClasspath
//    }
}
