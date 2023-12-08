# Back-in-time-debug-plugin

This plugin makes it easier to track the changes of properties' values during the execution of a program.

## Features

- tracking the changes of properties' values
- force setting the value of a property

## How to use

Annotate the target class with `@DebuggableStateHolder` annotation.
The classes annotated with this annotation will be modified by this plugin.
The plugin will insert the code to capture the changes of properties' values automatically.
Also, the plugin will add the methods to force setting the value of a property.

Here is the example:

```kotlin
@DebuggableStateHolder
class MyStateHolder {
    var counter: Int = 0

    fun increment() {
        myProperty++
    }
}
```

After the compilation, the plugin will modify the class as follows(not exactly the same. just an example):

```kotlin
class MyStateHolder : DebuggableStateHolderManipulator {
    var counter: Int = 0

    init {
        BackInTimeDebugService.register(this)
    }

    fun increment() {
        myProperty++
        BackInTimeDebugService.notifyPropertyChanged(this, "counter", counter)
    }

    fun forceSetPropertyValueForBackInTimeDebug(propertyName: String, value: Any?) {
        when (propertyName) {
            "counter" -> counter = value as Int
        }
    }
}
```

## Installation

### Manual Publishing

This plugin is still under development, so it is not available on Maven Central yet(planning to release it in the near future).
You can manually publish it to your local Maven repository by running the following command in the project's root directory:

```shell
./gradlew publishToMavenLocal
```

Also, you need to publish the runtime library as well:

```shell
cd back-in-time.library
./gradlew publishToMavenLocal
```

Now it is all set up. You can use it by configuring your gradle build script correctly.

### Configure Gradle

#### Enable K2 Compiler

This plugin is built with K2 compiler APIs. You need to enable K2 compiler to use this plugin.
Add the following line to your `build.gradle.kts` file:

```kotlin
kotlin {
    sourceSets.configureEach {
        languageSettings.languageVersion = "2.0"
    }
}
```

#### Apply the plugin

First, add the following line to your `settings.gradle.kts` file:

```kotlin
buildscript {
    repositories {
        mavenLocal()
    }
    dependencies {
        classpath("com.github.kitakkun:back-in-time-plugin:$backInTimeVersion")
    }
}
```

Then, apply the plugin in your `build.gradle.kts` file:

```kotlin
// because back-in-time-plugin is published to mavenLocal
// we must apply back-in-time-plugin here instead of plugins block
apply(plugin = "back-in-time-plugin")
```

#### Add the runtime library

Add the following line to your `build.gradle.kts` file:

```kotlin
repositories {
    mavenLocal()
}

dependencies {
    implementation("com.github.kitakkun:back-in-time-library:$backInTimeVersion")
}

```

#### Add the flipper dependencies

This plugin is designed to work with [Flipper](https://fbflipper.com/).
You need to add the following dependencies to your `build.gradle.kts` file:

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    debugImplementation("com.facebook.flipper:flipper:0.240.0")
    debugImplementation("com.facebook.soloader:soloader:0.10.5")

    releaseImplementation("com.facebook.flipper:flipper-noop:0.240.0")
}
```

## Implement required classes

You need to implement a Flipper plugin class to use this plugin.
The plugin class must extend `com.github.kitakkun.backintime.flipper.BackInTimeFlipperPlugin` class.

You have to implement the following methods:

```kotlin
abstract fun serializeValue(value: Any?, valueType: String): String
abstract fun deserializeValue(value: String, valueType: String): Any?
```

These methods are used to serialize and deserialize the values of properties.
`valueType` is the fully qualified name of the type of the value managed by the property.

Basically, you can use Gson to serialize and deserialize the values.
Here is the example implementation:

```kotlin
class MyFlipperPlugin : BackInTimeFlipperPlugin() {
    private val gson = Gson()

    override fun serializeValue(value: Any?, valueType: String): String {
        return gson.toJson(value)
    }

    override fun deserializeValue(value: String, valueType: String): Any? {
        return gson.fromJson(value, Class.forName(valueType))
    }
}
```

However, gson cannot serialize and deserialize kotlin-specific types such as `kotlin.Int`.
When you try to serialize and deserialize such types, `ClassNotFoundException` will be thrown.

To avoid this problem, you may need to map kotlin-specific types to java types.
Here is the example implementation:

```kotlin
class MyFlipperPlugin : BackInTimeFlipperPlugin() {
    private val gson = Gson()

    override fun serializeValue(value: Any?, valueType: String): String {
        return gson.toJson(value)
    }

    override fun deserializeValue(value: String, valueType: String): Any? {
        return when (valueType) {
            "kotlin.Int" -> gson.fromJson(value, Int::class.java)
            "kotlin.Long" -> gson.fromJson(value, Long::class.java)
            "kotlin.Float" -> gson.fromJson(value, Float::class.java)
            "kotlin.Double" -> gson.fromJson(value, Double::class.java)
            "kotlin.Boolean" -> gson.fromJson(value, Boolean::class.java)
            "kotlin.Char" -> gson.fromJson(value, Char::class.java)
            "kotlin.String" -> gson.fromJson(value, String::class.java)
            else -> gson.fromJson(value, Class.forName(valueType))
        }
    }
}
```

Only one thing left to do is to register the plugin.
Same as the other Flipper plugins, you can register it by calling `FlipperClient#addPlugin` method.
Below is the example to activate the plugin in the `Application` class:

```kotlin
class MyApplication : Application {
    override fun onCreate() {
        super.onCreate()
        SoLoader.init(this, false)

        if (BuildConfig.DEBUG && FlipperUtils.shouldEnableFlipper(this)) {
            val client = AndroidFlipperClient.getInstance(this)
            client.addPlugin(MyFlipperPlugin())
            client.start()
        }
    }
}
```

Now all set up. Let's build and run the app!

## Usage

Coming soon...
