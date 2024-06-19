plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.backintimeLint)
    `maven-publish`
}

dependencies {
    implementation(project(":backintime-plugin:common"))
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.compiler.embeddable)
    implementation(libs.kotlinx.serialization.json)

    compileOnly(libs.auto.service)
    ksp(libs.auto.service.ksp)
}

kotlin.compilerOptions {
    freeCompilerArgs = listOf(
        "-Xcontext-receivers",
        "-Xopt-in=org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI",
    )
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "backintime-compiler"
            from(components["kotlin"])
        }
    }
}
