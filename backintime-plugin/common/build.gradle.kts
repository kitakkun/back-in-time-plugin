plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.backintimeLint)
    `maven-publish`
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "backintime-plugin-common"
            from(components["kotlin"])
        }
    }
}
