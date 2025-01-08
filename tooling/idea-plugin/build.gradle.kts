plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.intelliJPlatform)
}

kotlin {
    jvmToolchain(17)
}

repositories {
    intellijPlatform {
        defaultRepositories()
    }
    mavenCentral()
}

dependencies {
    intellijPlatform {
        create("IC", "2024.3.1")
    }
}
