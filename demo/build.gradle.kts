import com.github.kitakkun.backintime.BackInTimeExtension

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
    capturedCalls += listOf(
        "com.github.kitakkun.back_in_time.ValueContainer:<set-value>",
        "com.github.kitakkun.back_in_time.ValueContainer:postValue",
    )
    valueGetters += listOf(
        "com.github.kitakkun.back_in_time.ValueContainer:<get-value>",
    )
}

application {
    mainClass = "com.github.kitakkun.back_in_time.MainKt"
}

dependencies {
    implementation("com.github.kitakkun.backintime:back-in-time.library:1.0.0")
    // coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.2")
}
