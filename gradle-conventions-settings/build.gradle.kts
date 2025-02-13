plugins {
    alias(libs.plugins.gradleKotlinDsl)
}

configurations.configureEach {
    resolutionStrategy {
        force(libs.kotlin.stdlib)
    }
}

dependencies {
    compileOnly(libs.jetbrains.compose.gradle.plugin)
}
