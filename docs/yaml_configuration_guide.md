# YAML Configuration Guide

The YAML Configuration file is used to specify the Trackable StateHolders and their specifications.
This information is necessary for the compiler to make library classes back-in-time debuggable.

See [the example YAML configuration file under the demo project](../demo/app/backintime-default-config.yaml). Basically, you can just copy and paste it to your project.

## Defining Trackable StateHolders

In the top level of the YAML file, we define the `trackableStateHolders` key.
This key contains a list of Trackable StateHolders, each defined by a class ID and its specifications.

```yaml
trackableStateHolders:
  - TODO
```

## Trackable StateHolder Specification

Each Trackable StateHolder is defined by the following keys:

- `classId`: The class ID of the Trackable StateHolder.
- `serializeAs`: The serializer type for serializing the value.
- `accessor`: The accessor methods for getting and setting the value.
- `captures`: The capture target methods for capturing the state changes.

```yaml
trackableStateHolders:
  - classId: TODO
    serializeAs: TODO
    accessor: TODO
    captures: TODO
```

### classId

The `classId` key specifies the class ID of the Trackable StateHolder.
This is the fully qualified name of the class, including the package name.

The package name should be split by `/` instead of `.`. `.` can only be used to specify nested classes.

Example: `com/example/A`, `com/example/A.B`

### serializeAs

The `serializeAs` key determines how the value of the Trackable StateHolder should be serialized.

You can use the following options for the `serializeAs` key:

- `0`: The first type parameter of the class. If it is `MutableStateFlow<Int>`, its value will be serialized as `Int`.
- classId: The class ID of the type. If it is `kotlin/collections/List`, its value will be serialized as `List`.

### accessor

The `accessor` key specifies the accessor methods for the Trackable StateHolder.

The `accessor` key contains two sub-keys:

- `getter`: The function or property signature of getter method.
- `setter`: The function or property signature of setter method.

The getter and setter methods are used to get and set the value of the Trackable StateHolder.

Functions signatures are defined as follows:

```yaml
receiverClassSignature functionSignature(argumentSignature)
```

Here is the examples of function signatures:

```kotlin
package com.example

class A {
    // Signature: com/example/A.getValue(kotlin/Int)
    fun method(arg: Int): Int = 0

    var property: Int
        // Signature: com/example/A.<get-property>
        get() = field
        // Signature: com/example/A.<set-property>
        set(value) {
            field = value
        }
}

// Signature: com/example/A com/example/getState()
fun A.extension(): Int = 0
```

### captures

WIP
