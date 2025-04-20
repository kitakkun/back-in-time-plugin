plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.backintimeLint)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    jvmToolchain(8)

    jvm()
    androidTarget()
}

android {
    namespace = "com.kitakkun.backintime.evaluation"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.kitakkun.backintime.evaluation"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug { }
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-P", "plugin:com.kitakkun.backintime.compiler:config=$projectDir/backintime-default-config.yaml")
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.runtime)
            implementation(projects.core.annotations)
            implementation(projects.core.websocket.event)
            implementation(projects.tooling.core.model)

            implementation(compose.material3)
            implementation(compose.uiTooling)
            implementation(compose.preview)
            implementation(libs.viewmodel)

            // room
            implementation(libs.androidx.room.runtime)

            // koin
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.kotlinx.serialization.json)
        }

        androidMain.dependencies {
            implementation(libs.koin.android)
            implementation(libs.core.ktx)
            implementation(libs.lifecycle.runtime.ktx)
            implementation(libs.activity.compose)
        }
    }
}

dependencies {
    kotlinCompilerPluginClasspath(projects.compiler.cli)
    ksp(libs.androidx.room.compiler)
}
