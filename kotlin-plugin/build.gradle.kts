plugins {
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.serialization")
    `maven-publish`
}

dependencies {
    implementation(project(":plugin-common"))
    implementation(kotlin("stdlib"))
    implementation(kotlin("compiler-embeddable"))

    compileOnly(libs.auto.service)
    kapt(libs.auto.service)

    implementation(libs.kotlinx.serialization.json)
    testImplementation(kotlin("test"))
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
