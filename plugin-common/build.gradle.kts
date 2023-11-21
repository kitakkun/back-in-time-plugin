plugins {
    kotlin("jvm")
    `maven-publish`
}

repositories {
    mavenLocal()
    mavenCentral()
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
