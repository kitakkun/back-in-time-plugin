plugins {
    alias(libs.plugins.backintimeCompilerModule)
    alias(libs.plugins.backintimePublication)
    alias(libs.plugins.kotlinSerialization)
}

backintimePublication {
    artifactId = "compiler-yaml"
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kaml)
    testImplementation(libs.kotlin.test)
}
