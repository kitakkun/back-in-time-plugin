plugins {
    alias(libs.plugins.gradleKotlinDsl)
}

dependencies {
    implementation(libs.kotlin.gradle.plugin)
    compileOnly(libs.ktlint.gradle)
    compileOnly(libs.maven.publish)
    implementation(libs.jetbrains.compose.gradle.plugin)
    implementation(libs.compose.compiler.gradle.plugin)
}
