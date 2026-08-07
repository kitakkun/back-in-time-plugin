plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.backintimePublication)
}

backintimePublication {
    artifactId = "compiler-yaml"
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kaml)
    testImplementation(libs.kotlin.test)
}
