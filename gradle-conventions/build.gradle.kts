plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(":gradle-conventions-settings")
    compileOnly(libs.ktlint.gradle)
    compileOnly(libs.maven.publish)
}
