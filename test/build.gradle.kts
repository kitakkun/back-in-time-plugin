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

    sourceSets.main {
        dependencies {
            implementation("com.github.kitakkun.backintime:back-in-time.library:1.0.0")
            // coroutines
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.2")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
        }
    }

    sourceSets.test {
        dependsOn(sourceSets.main.get())
        kotlin {
            setSrcDirs(listOf("src/test/kotlin", "build/classes/kotlin/main"))
        }
        dependencies {
            implementation(kotlin("test"))
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.2")
        }
    }
}

configure<BackInTimeExtension> {
    enabled = true
    capturedCalls += listOf(
        "com.github.kitakkun.backintime.test.GradleConfiguredValueContainer:<set-value>",
        "com.github.kitakkun.backintime.test.GradleConfiguredValueContainer:update",
    )
    valueGetters += listOf(
        "com.github.kitakkun.backintime.test.GradleConfiguredValueContainer:<get-value>",
    )
    valueSetters += listOf(
        "com.github.kitakkun.backintime.test.GradleConfiguredValueContainer:<set-value>",
    )
}
