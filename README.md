# back-in-time-plugin

No more print debugging, No more repetitive manual debugging.

This plugin helps you to track the changes of application state during its execution.
Also, you can easily revert the state to the previous one. We call it "back-in-time" debugging.

This plugin currently intended to be used with Android projects.
But we are planning to support other platforms in the future.

Debugging tool is available at [flipper-plugin-back-in-time](https://github.com/kitakkun/flipper-plugin-back-in-time).
Want to play with it? Android example is available at `back-in-time-demo` module in this repository.

> [!IMPORTANT]
> This project is still a work in in progress, and its API is unstable and may change without any notice.
> Also, we are planning make back-in-time-debugger implementation independent from Flipper to support other platforms (iOS, Desktop, JS...).
> Using this plugin for a hobby project is fine, but we do not recommend using it for production projects yet.

## How to use

### Manual Publishing Artifacts

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
        kotlin("plugin.serialization") version "1.9.22" apply false // required by the plugin
    }
}

dependencyResolutionManagement {
    repositories {
        mavenLocal() // annotation library and runtime library are also published to Maven Local
        // ...
    }
}
```

> build.gradle.kts

```kotlin
plugins {
    id("com.github.kitakkun.backintime") version "1.0.0"
    kotlin("plugin.serialization")
    ...
}

// add required dependencies
dependencies {
    // Note that annotations and runtime library are automatically added by the back-in-time gradle plugin

    // other dependencies required to use the plugin
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")
    // debugger is implemented as a Flipper plugin, so you need this
    debugImplementation("com.facebook.flipper:flipper:$flipperVersion")
    debugImplementation("com.facebook.soloader:soloader:$soloaderVersion")
    releaseImplementation("com.facebook.flipper:flipper-noop:$flipperVersion")
}

backInTime {
    enabled = true // default is true
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

Annotate your class with `@DebuggableStateHolder` to make it back-in-time debuggable.
Make sure property you want to debug is holding serializable value by kotlinx.serialization.

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

### One more step (Setup Flipper)

Currently, this plugin is completely dependent on Flipper.
You need to setup Flipper to use this plugin.
See [Flipper](https://fbflipper.com/) for more information.

You can use pre-built FlipperPlugin implementation class `BackInTimeDebugFlipperPlugin` to add the back-in-time debugging feature to your Flipper.
Also, debugging tool is available at [flipper-plugin-back-in-time](https://github.com/kitakkun/flipper-plugin-back-in-time).

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
    // other required properties for debugging...

    init {
        BackInTimeDebugService.emitEvent(DebuggableStateHolderEvent.RegisterInstance(...))
    }

    fun increment() {
        val callUUID = UUID.randomUUID().toString()
        BackInTimeDebugService.emitEvent(DebuggableStateHolderEvent.MethodCall(...))
        count++
        BackInTimeDebugService.emitEvent(DebuggableStateHolderEvent.PropertyValueChange(...))
    }

    fun forceSetValue(propertyName: String, value: Any?) {
        when (propertyName) {
            "count" -> if (value is Int) count = value
        }
    }

    // other required methods for debugging...
}
```

### Runtime

At runtime, inserted code works to enable the back-in-time debugging.
The debugger is implemented as a Flipper plugin, so you can debug your app via Flipper.

