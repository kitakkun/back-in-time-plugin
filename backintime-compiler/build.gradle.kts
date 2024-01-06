plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinKapt)
    alias(libs.plugins.kotlinSerialization)
    `maven-publish`
}

dependencies {
    implementation(project(":backintime-plugin-common"))
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.compiler.embeddable)
    implementation(libs.kotlinx.serialization.json)

    compileOnly(libs.auto.service)
    kapt(libs.auto.service)
}

tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class).all {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xcontext-receivers")
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "backintime-compiler"
            from(components["kotlin"])
        }
    }
}
