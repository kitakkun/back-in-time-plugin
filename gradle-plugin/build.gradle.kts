plugins {
    kotlin("jvm")
    kotlin("kapt")
    `java-gradle-plugin`
    `maven-publish`
}

gradlePlugin {
    plugins {
        create("backInTime") {
            id = "com.github.kitakkun.backintime"
            implementationClass = "com.github.kitakkun.backintime.BackInTimePlugin"
        }
    }
}

dependencies {
    implementation(project(":plugin-common"))
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin-api")

    compileOnly("com.google.auto.service:auto-service:1.1.1")
    kapt("com.google.auto.service:auto-service:1.1.1")
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["kotlin"])
            groupId = "com.github.kitakkun.backintime"
            artifactId = "com.github.kitakkun.backintime.gradle.plugin"
            version = "1.0.0"
        }
    }
    repositories {
        mavenLocal()
    }
}
