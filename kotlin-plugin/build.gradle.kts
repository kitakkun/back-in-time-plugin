plugins {
    kotlin("jvm")
    kotlin("kapt")
    `maven-publish`
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.0")
    implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:1.9.0")

    compileOnly("com.google.auto.service:auto-service:1.1.1")
    kapt("com.google.auto.service:auto-service:1.1.1")

    testImplementation(kotlin("test"))
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["kotlin"])
            groupId = "com.github.kitakkun"
            artifactId = "back-in-time-kotlin-plugin"
            version = "1.0.0"
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
