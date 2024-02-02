# back-in-time-plugin

No more print debugging, No more repetitive manual debugging.

This plugin helps you to track the changes of application state during its execution.
Also, you can easily revert the state to the previous one. We call it "back-in-time" debugging.

This plugin currently intended to be used with Android projects.

## How to use

### Manual Publishing

This plugin is still under development, and its artifacts does not exist on Maven Central yet.
You can manually publish them to your local Maven repository by running the following command in the project's root directory:

```shell
./gradlew publishToMavenLocal
```

### Configure Gradle

> settings.gradle.kts

```kotlin
pluginManagement {
    repositories {
        mavenLocal() // plugin is published to Maven Local
        // ...
    }
    plugins {
        id("com.github.kitakkun.backintime") version "1.0.0" apply false
        kotlin("plugin.serialization") version "1.9.21" apply false // required by the plugin
    }
}

dependencyResolutionManagement {
    repositories {
        mavenLocal() // library is also published to Maven Local
        // ...
    }
}
```

> build.gradle.kts

```kotlin
plugins {
    id("com.github.kitakkun.backintime")
    kotlin("plugin.serialization")
    ...
}

// add dependencies
dependencies {
    debugImplementation("com.github.kitakkun.backintime:backintime-runtime:$backInTimeVersion")
    implementation("com.github.kitakkun.backintime:backintime-annotations:$backInTimeVersion")

    // required other dependencies
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")
    debugImplementation("com.facebook.flipper:flipper:$flipperVersion")
    debugImplementation("com.facebook.soloader:soloader:$soloaderVersion")
    releaseImplementation("com.facebook.flipper:flipper-noop:$flipperVersion")
}

// disable the plugin for release build (need this to avoid release build error)
android {
    ...
    buildTypes {
        debug {
            backInTime.enabled = true
        }
        release {
            backInTime.enabled = false
        }
    }
}

backInTime {
    valueContainers {
        androidValueContainers() // support for MutableLiveData, MutableStateFlow, MutableState
        composeMutableStates()   // support for MutableState, MutableIntState, MutableLongState, etc...
        collections()            // support for MutableList, MutableMap, MutableSet

        // You can also add your own value container
        container {
            className = "com.example.MyValueContainer"
            captures = listOf("<set-value>", "updateValue")
            getter = "<get-value>"
            setter = "<set-value>"
        }
    }
}
```

### Annotate your class

Annotate your class with `@DebuggableStateHolder` to enable the back-in-time debugging.
Make sure property you want to debug is holding serializable value.

```kotlin
@DebuggableStateHolder
class CounterViewModel : ViewModel() {
    private val mutableCount = MutbaleStateFlow(0)
    val count = mutableCount.asStateFlow()

    fun increment() {
        count.value++
    }
}
```

## How it works

This plugin comes with two phases: compile-time and runtime.

### Compile-time

At compile-time, this plugin finds the classes annotated with `@DebuggableStateHolder` and generates the code to track the changes of its state.
For example, if you have the following class:

```kotlin
@DebuggableStateHolder
class CounterViewModel {
    var count = 0

    fun increment() {
        count++
    }
}
```

The plugin modify the class as follows(not exact the same, just for explanation):

```kotlin
@DebuggableStateHolder
class CounterViewModel : BackInTimeDebuggable {
    var count = 0

    init {
        BackInTimeDebugService.register(this, InstanceInfo(...))
    }

    fun increment() {
        val callUUID = UUID.randomUUID().toString()
        BackInTimeDebugService.notifyMethodCall(this, "increment", callUUID)
        count++
        BackInTimeDebugService.notifyPropertyChange(this, "count", count, callUUID)
    }
}
```

### Runtime

At runtime, inserted code works to enable the back-in-time debugging.
The debugger is implemented as a Flipper plugin, so you can debug your app via Flipper.

