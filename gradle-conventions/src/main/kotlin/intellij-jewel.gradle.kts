import util.libs

plugins {
    id("intellij-common")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

dependencies {
    implementation(libs.findLibrary("jewel-laf").get()) {
        exclude(group = "org.jetbrains.kotlinx")
        exclude(group = "com.intellij.ide")
    }
    implementation(libs.findLibrary("jewel-standalone").get()) {
        exclude(group = "org.jetbrains.kotlinx")
    }
    api(compose.desktop.currentOs) {
        exclude(group = "org.jetbrains.compose.material")
        exclude(group = "org.jetbrains.kotlinx")
    }
}
