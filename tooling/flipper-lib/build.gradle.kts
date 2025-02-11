plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    js(IR) {
        nodejs()
        generateTypeScriptDefinitions()
        binaries.library()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.tooling.core.model)
            implementation(projects.core.websocket.event)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(libs.uuid)
        }

        jsMain.dependencies {
            implementation(libs.kotlin.react)
            implementation(npm("flipper-plugin", "latest"))
        }
    }

    compilerOptions {
        freeCompilerArgs.add("-opt-in=kotlin.js.ExperimentalJsExport")
    }
}

// workaround for unresolved PluginClient type error in the generated type definitions when executing `yarn tsc`.
val tscWorkaroundTask = task("tscErrorWorkaround") {
    onlyIf {
        !tasks.named("jsNodeProductionLibraryDistribution").get().state.skipped
    }
    doFirst {
        val outputTypeDefinitionFile = file("$projectDir/build/dist/js/productionLibrary/backintime-tooling-flipper-lib.d.ts")
        val lines = outputTypeDefinitionFile.readLines().toMutableList()
        lines.replaceAll { line ->
            if (line.contains("constructor(flipperClient: PluginClient<")) {
                val indent = line.takeWhile { it.isWhitespace() }
                "$indent// @ts-ignore\n$line"
            } else {
                line
            }
        }
        outputTypeDefinitionFile.writeText(lines.joinToString("\n"))
    }
}

tasks.named("jsNodeProductionLibraryDistribution").get().finalizedBy(tscWorkaroundTask)
