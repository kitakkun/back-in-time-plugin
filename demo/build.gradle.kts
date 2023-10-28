import com.github.kitakkun.back_in_time.BackInTimeExtension

buildscript {
    repositories {
        mavenLocal()
    }
    dependencies {
        classpath("com.github.kitakkun:back-in-time-plugin:1.0.0")
    }
}

plugins {
    kotlin("jvm")
    id("application")
}

// because back-in-time-plugin is published to mavenLocal
// we must apply back-in-time-plugin here instead of plugins block
apply(plugin = "back-in-time-plugin")

repositories {
    mavenCentral()
    mavenLocal()
}

kotlin {
    sourceSets.all {
        languageSettings.languageVersion = "2.0"
    }
}

configure<BackInTimeExtension> {
    enabled = true
    annotations = listOf("HogeAnnotation")
}

application {
    mainClass = "com.github.kitakkun.back_in_time.MainKt"
}

dependencies {
    implementation("com.github.kitakkun.back_in_time:annotations:1.0.0")
}
