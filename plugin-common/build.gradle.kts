plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    `maven-publish`
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["kotlin"])
            groupId = "com.github.kitakkun"
            artifactId = "back-in-time-plugin-common"
            version = "1.0.0"
        }
    }
    repositories {
        mavenLocal()
    }
}
