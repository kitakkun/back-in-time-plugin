plugins {
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.serialization")
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
    implementation(kotlin("gradle-plugin-api"))

    compileOnly(libs.auto.service)
    kapt(libs.auto.service)
    implementation(libs.kotlinx.serialization.json)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = "com.github.kitakkun.backintime"
            artifactId = "com.github.kitakkun.backintime.gradle.plugin"
            version = "1.0.0"

            from(components["kotlin"])
        }
    }
    repositories {
        mavenLocal()
    }
}
