# BackInTime Compiler Basics

In the brief, the back-in-time compiler performs two key tasks during the compile phase:

- Adds necessary methods and properties to classes to enable back-in-time debugging.
- Inserts state capture calls after properties of the class annotated with `@BackInTime` might
  change.

Let's break down one by one.

## Phase1: Make a class back-in-time debuggable (FIR/IR)

The first thing that the compiler does is adding an interface `BackInTimeDebuggable` to your class.

```kotlin
interface BackInTimeDebuggable {
    val backInTimeInstanceUUID: String
    val backInTimeInitializedPropertyMap: MutableMap<String, Boolean>

    fun forceSetValue(propertyName: String, value: Any?)
    fun serializeValue(propertyName: String, value: Any?): String
    fun deserializeValue(propertyName: String, value: String): Any?
}
```

`BackInTimeFirSupertypeGenerationExtension` will do this job:

```kotlin
@BackInTime
class A { ... }
↓
@BackInTime
class A : BackInTimeDebuggable { ... }
```

Next, `BackInTimeFirDeclarationGenerationExtension` will add declarations which have to be
overridden:

```kotlin
@BackInTime
class A : BackInTimeDebuggable { ... }
↓
@BackInTime
class A : BackInTimeDebuggable {
    override val backInTimeInstanceUUID: String
    override val backInTimeInitializedPropertyMap: MutableMap<String, Boolean>

    override fun forceSetValue(propertyName: String, value: Any?)
    override fun serializeValue(propertyName: String, value: Any?): String
    override fun deserializeValue(propertyName: String, value: String): Any?
    ...
}
```

Lastly, inside `BackInTimeIrGenerationExtension`, `BackInTimeDebuggableImplementTransformer` will
add implementations of each methods and properties.

## Phase2: Track state changes and insert capture calls (IR)

The most complicated area in the back-in-time compiler is here.

- `BackInTimeDebuggableConstructorTransformer` adds instance registration call in the constructors. Also resolve the
  relationships among back-in-time debuggable instances.
- `BackInTimeCaptureMethodInvocationTransformer` adds
- 
