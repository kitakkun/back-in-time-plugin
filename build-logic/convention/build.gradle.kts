plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        register("io.github.kitakkun.backintime.conventions.ktlint") {
            id = "backintime.lint"
            implementationClass = "io.github.kitakkun.backintime.convention.LintConventionPlugin"
        }
    }
}

dependencies {
    compileOnly(libs.ktlint.gradle)
}
