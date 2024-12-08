plugins {
    `kotlin-dsl`
    alias(libs.plugins.buildconfig)
}

gradlePlugin {
    plugins {
        register("com.kitakkun.backintime.conventions.ktlint") {
            id = "backintime.lint"
            implementationClass = "com.kitakkun.backintime.convention.LintConventionPlugin"
        }
        register("com.kitakkun.backintime.conventions.publication") {
            id = "backintime.publication"
            implementationClass = "com.kitakkun.backintime.convention.BackInTimePublicationPlugin"
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
