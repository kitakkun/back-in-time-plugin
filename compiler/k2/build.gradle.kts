plugins {
    alias(libs.plugins.backintimeCompilerModule)
    alias(libs.plugins.backintimePublication)
}

backintimePublication {
    artifactId = "compiler-k2"
}

kotlin.compilerOptions {
    freeCompilerArgs = listOf("-Xcontext-receivers")
}

dependencies {
    implementation(libs.kotlin.compiler.embeddable)
    implementation(projects.compiler.common)
}