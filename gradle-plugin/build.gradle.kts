plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.backintimeLint)
    alias(libs.plugins.javaGradlePlugin)
    alias(libs.plugins.buildconfig)
    alias(libs.plugins.backintimePublication)
    alias(libs.plugins.gradleTestKitSupport)
}

gradlePlugin {
    plugins {
        create("backInTime") {
            id = "com.kitakkun.backintime"
            implementationClass = "com.kitakkun.backintime.gradle.BackInTimeGradlePlugin"
        }
    }
}

dependencies {
    implementation(libs.kotlin.gradle.plugin.api)
    implementation(libs.kotlin.gradle.plugin)

    functionalTestImplementation(libs.gradle.testkit.support)
    functionalTestImplementation(libs.gradle.testkit.truth)
    functionalTestImplementation(libs.kotlin.test.junit)
}

buildConfig {
    generateAtSync.set(true)
    useKotlinOutput()
    sourceSets.getByName("functionalTest") {
        className("BuildConfigForTest")
        buildConfigField("VERSION", libs.versions.backintime.get())
        buildConfigField("KOTLIN_VERSION", libs.versions.kotlin.get())
    }
    buildConfigField("VERSION", libs.versions.backintime.get())
}

tasks.test {
    useJUnitPlatform()
}

backintimePublication {
    artifactId = "gradle-plugin"
}
