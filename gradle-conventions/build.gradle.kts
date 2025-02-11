plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(":gradle-conventions-settings")
    implementation(libs.kotlin.gradle.plugin)
    compileOnly(libs.ktlint.gradle)
    compileOnly(libs.maven.publish)
    implementation(libs.jetbrains.compose.gradle.plugin)
    implementation(libs.compose.compiler.gradle.plugin)
}
