plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.backintimeLint)
    `maven-publish`
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "backintime-plugin-common"
            from(components["kotlin"])
        }
    }
}
