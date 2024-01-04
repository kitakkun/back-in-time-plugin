plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinKapt)
    alias(libs.plugins.kotlinSerialization)
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
    implementation(libs.kotlin.gradle.plugin.api)
    implementation(libs.kotlinx.serialization.json)

    compileOnly(libs.auto.service)
    kapt(libs.auto.service)
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
