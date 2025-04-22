# Trackable StateHolder

This documentation describes the concept of a Trackable StateHolder and its necessity for back-in-time debugging.

## What are Trackable StateHolders?

Library classes cannot be back-in-time debuggable because they are already compiled.

`MutableStateFlow` from kotlinx-coroutines, and `MutableLiveData` from AndroidX are good examples.
They are provided as binaries or compiled sources, so basically not modifiable by the Kotlin Compiler Plugin.

Though it is not easy to make them back-in-time debuggable,
we can still make them back-in-time debuggable by using a signature-based approach.

We call such classes "Trackable StateHolder".

## Signature-based approach

Signature-based approach is a kind of naive approach to make library classes back-in-time debuggable.

Essentially, for the back-in-time debugging, we need to let the compiler know the following:

- What methods can cause the state changes?
- How to get the current state?
- How to set a new state?

If we know all of these, we can make the class back-in-time debuggable.

For example, if we have a class `A` with a `MutableStateFlow`:

```kotlin
// Note that this is a pseudo code
@BackInTime
class A : BackInTimeDebuggable {
    val counter = MutableStateFlow(0)

    fun increment() {
        counter.value++
        capture(counter.value) // compiler-generated function call
    }

    // compiler-generated method
    override fun forceSetValue(propertySignature: String, jsonValue: String) {
        when (propertySignature) {
            "counter" -> counter.value = deserialize<Int>(jsonValue)
        }
    }
}
```

The compiler knows that `counter` is a `MutableStateFlow` and it can be changed by calling `.value++`.
So, it can capture the state changes by calling `capture(counter.value)` after each value assignment.

Also, because the compiler knows how to set a new state, it can perform back-in-time debugging by calling the setter of `counter.value` in the generated `forceSetValue` method.

## How to let the compiler know Trackable StateHolders' specifications?

To let the compiler know the specifications of Trackable StateHolders, we need to define them in a yaml configuration file.
Here is an example of a yaml configuration file:

```yaml
trackableStateHolders:
  - classId: "kotlinx/coroutines/flow/MutableStateFlow"
    serializeAs: "0"
    accessor:
      getter: "<get-value>"
      setter: "<set-value>"
    captures:
      - signature: "<set-value>"
        strategy: "arg0"
      - signature: "kotlinx/coroutines/flow/update"
        strategy: "afterCall"
      - signature: "kotlinx/coroutines/flow/updateAndGet"
        strategy: "afterCall"
      - signature: "kotlinx/coroutines/flow/getAndUpdate"
        strategy: "afterCall"
      - signature: "emit"
        strategy: "arg0"
      - signature: "tryEmit"
        strategy: "arg0"
```

Typical trackable stateHolders:

- `androidx.lifecycle.MutableLiveData`
- `androidx.lifecycle.MutableStateFlow`
- `androidx.compose.runtime.MutableState`
- `kotlinx.coroutines.flow.MutableStateFlow`
- `kotlinx.coroutines.flow.MutableSharedFlow`
- `kotlin.collections.MutableList`
- `kotlin.collections.MutableMap`
- `kotlin.collections.MutableSet`

For more details, please refer to the [yaml configuration guide](./yaml_configuration_guide.md).

## Why choose naive signature-based approach?

You may wonder why we settled on the signature based approach. There are several reasons behind this decision.

### Why we go with such a naive approach?

Of course, the signature based approach is a naive approach to make external classes back-in-time debuggable.
It is not a perfect solution, but it is a good enough solution for most of the cases.

Actually, pure variables are captured with the same approach:

```kotlin
@BackInTime
class CounterViewModel : ViewModel() {
    var count = 0

    fun increment() {
        count++
        capture(field) // compiler generated code
    }
}
```

The part of `count++` is detected as `IrSetField` and we can insert capture call after it easily.

### Why not use observable features to capture state changes?

For example, we can observe or collect the state changes if it is a observable stateholder:

```kotlin
@BackInTime
class CounterViewModel : ViewModel() {
    val count = MutableStateFlow(0)

    init {
        backInTimeScope.laucnh {
            count.collect { value ->
                // capture value changes
            }
        }
    }

    fun increment() {
        count.value++
    }
}
```

In this case, there are two problems:

- We need to have coroutine scope to observe the state changes. But we don't know when to cancel the observation.
- We want to know where the state changes happened. But we can't know that.

If it is a ViewModel, we can use `viewModelScope` to safely observe the state changes.
But it is all about Android projects. The Back-in-time plugin is not limited to Android projects.
