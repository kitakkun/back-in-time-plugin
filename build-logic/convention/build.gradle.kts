plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        register("com.github.kitakkun.backintime.conventions.ktlint") {
            id = "backintime.lint"
            implementationClass = "com.github.kitakkun.backintime.convention.LintConventionPlugin"
        }
    }
}

dependencies {
    compileOnly(libs.ktlint.gradle)
}
