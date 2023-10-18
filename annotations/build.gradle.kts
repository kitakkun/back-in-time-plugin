plugins {
    kotlin("jvm")
    `maven-publish`
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["kotlin"])
            groupId = "com.github.kitakkun.back_in_time"
            artifactId = "annotations"
            version = "1.0.0"
        }
    }
    repositories {
        mavenLocal()
    }
}
