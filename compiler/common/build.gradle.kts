plugins {
    alias(libs.plugins.backintimeCompilerModule)
    alias(libs.plugins.backintimePublication)
}

backintimePublication {
    artifactId = "compiler-common"
}

dependencies {
    implementation(libs.kotlin.compiler.embeddable)
}