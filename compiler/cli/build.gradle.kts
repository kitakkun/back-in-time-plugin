plugins {
    alias(libs.plugins.backintimeCompilerModule)
    alias(libs.plugins.backintimePublication)
}

backintimePublication {
    artifactId = "compiler-cli"
}

dependencies {
    implementation(libs.kotlin.compiler.embeddable)
    implementation(projects.compiler.backend)
    implementation(projects.compiler.k2)
    implementation(projects.compiler.common)
}