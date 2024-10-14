plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.backintimeLint)
    alias(libs.plugins.backintimePublication)
}

backintimePublication {
    artifactId = "gradle-plugin"
}
