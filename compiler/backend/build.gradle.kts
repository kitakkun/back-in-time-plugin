plugins {
    alias(libs.plugins.backintimeCompilerModule)
    alias(libs.plugins.backintimePublication)
}

backintimePublication {
    artifactId = "compiler-backend"
}

kotlin.compilerOptions {
    freeCompilerArgs = listOf(
        "-Xcontext-receivers",
        "-opt-in=org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI",
    )
}

dependencies {
    implementation(libs.kotlin.compiler.embeddable)
    implementation(projects.compiler.common)
    implementation(projects.compiler.yaml)
    implementation(projects.core.annotations)
}