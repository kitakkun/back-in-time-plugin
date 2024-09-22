plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.backintimeLint)
    alias(libs.plugins.mavenPublish)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "backintime-plugin-common"
            from(components["kotlin"])
        }
    }
}
