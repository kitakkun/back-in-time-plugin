plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinKapt)
    alias(libs.plugins.kotlinSerialization)
    `maven-publish`
}

dependencies {
    implementation(project(":plugin-common"))
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.compiler.embeddable)
    implementation(libs.kotlinx.serialization.json)

    compileOnly(libs.auto.service)
    kapt(libs.auto.service)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = "com.github.kitakkun.backintime"
            artifactId = "kotlin.plugin"
            version = "1.0.0"

            from(components["kotlin"])
        }
    }
    repositories {
        mavenLocal()
    }
}

tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class).all {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xcontext-receivers")
    }
}
