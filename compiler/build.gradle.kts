plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.backintimeLint)
    alias(libs.plugins.backintimePublication)
    alias(libs.plugins.buildconfig)
}

dependencies {
    implementation(projects.pluginCommon)
    implementation(projects.core.annotations)
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.compiler.embeddable)
    implementation(libs.kotlinx.serialization.json)

    compileOnly(libs.auto.service)
    ksp(libs.auto.service.ksp)
}

kotlin.compilerOptions {
    freeCompilerArgs = listOf(
        "-Xcontext-receivers",
        "-opt-in=org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI",
    )
}

buildConfig {
    buildConfigField("VERSION", libs.versions.backintime.get())
}

backintimePublication {
    artifactId = "compiler"
}
