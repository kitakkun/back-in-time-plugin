import util.libs

plugins {
    id("intellij-jewel")
}

dependencies {
    implementation(project(":tooling:core:ui"))
    implementation(project(":tooling:core:model"))
    implementation(project(":tooling:core:usecase"))
    implementation(project(":tooling:core:shared"))
    implementation(project(":tooling:core:database"))
    implementation(libs.findLibrary("kotlinx-serialization-json").get())
}
