plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinSerialization)
    `maven-publish`
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = "com.github.kitakkun.backintime"
            artifactId = "backintime-plugin-common"
            version = "1.0.0"

            from(components["kotlin"])
        }
    }
    repositories {
        mavenLocal()
    }
}
