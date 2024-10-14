plugins {
    `kotlin-dsl`
    alias(libs.plugins.buildconfig)
}

gradlePlugin {
    plugins {
        register("io.github.kitakkun.backintime.conventions.ktlint") {
            id = "backintime.lint"
            implementationClass = "io.github.kitakkun.backintime.convention.LintConventionPlugin"
        }
        register("io.github.kitakkun.backintime.conventions.publication") {
            id = "backintime.publication"
            implementationClass = "io.github.kitakkun.backintime.convention.BackInTimePublicationPlugin"
        }
    }
}

dependencies {
    compileOnly(libs.ktlint.gradle)
    compileOnly(libs.maven.publish)
}

buildConfig {
    buildConfigField("VERSION", libs.versions.backintime.get())
}
