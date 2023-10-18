plugins {
    kotlin("jvm")
    kotlin("kapt")
    `java-gradle-plugin`
    `maven-publish`
}

gradlePlugin {
    plugins {
        create("backInTime") {
            id = "back-in-time-plugin"
            implementationClass = "com.github.kitakkun.back_in_time.BackInTimePlugin"
        }
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin-api")

    compileOnly("com.google.auto.service:auto-service:1.1.1")
    kapt("com.google.auto.service:auto-service:1.1.1")
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["kotlin"])
            groupId = "com.github.kitakkun"
            artifactId = "back-in-time-plugin"
            version = "1.0.0"
        }
    }
    repositories {
        mavenLocal()
    }
}
