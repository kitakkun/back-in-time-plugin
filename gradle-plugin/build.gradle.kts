plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.backintimeLint)
    alias(libs.plugins.javaGradlePlugin)
    alias(libs.plugins.buildconfig)
    alias(libs.plugins.backintimePublication)
}

gradlePlugin {
    plugins {
        create("backInTime") {
            id = "io.github.kitakkun.backintime"
            implementationClass = "io.github.kitakkun.backintime.gradle.BackInTimeGradlePlugin"
        }
    }
}

dependencies {
    implementation(projects.pluginCommon)
    implementation(libs.kotlin.gradle.plugin.api)
    implementation(libs.kotlin.gradle.plugin)

    testImplementation(gradleTestKit())
    testImplementation("org.spockframework:spock-core:2.2-groovy-3.0") {
        exclude(group = "org.codehaus.groovy")
    }
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}

buildConfig {
    buildConfigField("VERSION", libs.versions.backintime.get())
    buildConfigField("COMPILER_PLUGIN_ID", "io.github.kitakkun.backintime.compiler")
}

tasks.test {
    useJUnitPlatform()
}

backintimePublication {
    artifactId = "gradle-plugin"
}