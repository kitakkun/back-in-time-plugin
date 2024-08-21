plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(project(":backintime-debugger:ui"))
            implementation(project(":backintime-debugger:router"))
            implementation(project(":backintime-debugger:resources"))
            implementation(project(":backintime-debugger:featurecommon"))
            implementation(project(":backintime-debugger:feature:instance"))
            implementation(project(":backintime-debugger:feature:settings"))
            implementation(project(":backintime-debugger:feature:log"))
            implementation(project(":backintime-debugger:feature:connection"))
        }
    }
}
