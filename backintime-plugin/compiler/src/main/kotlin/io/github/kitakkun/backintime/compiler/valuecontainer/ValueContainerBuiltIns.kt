package io.github.kitakkun.backintime.compiler.valuecontainer

import io.github.kitakkun.backintime.compiler.valuecontainer.match.function
import io.github.kitakkun.backintime.compiler.valuecontainer.match.memberFunction
import io.github.kitakkun.backintime.compiler.valuecontainer.match.memberPropertyGetter
import io.github.kitakkun.backintime.compiler.valuecontainer.match.memberPropertySetter
import io.github.kitakkun.backintime.compiler.valuecontainer.raw.CaptureStrategy
import io.github.kitakkun.backintime.compiler.valuecontainer.raw.selfContainedValueContainer
import io.github.kitakkun.backintime.compiler.valuecontainer.raw.valueContainer
import org.jetbrains.kotlin.javac.resolve.classId
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

val ValueContainerBuiltIns = listOf(
    // androidx
    valueContainer(classId("androidx.lifecycle", "MutableLiveData")) {
        getter = memberFunction("getValue")
        setter = listOf(memberFunction("postValue"))
        captureTargets = listOf(
            memberFunction("setValue") to CaptureStrategy.ValueArgument(),
            memberFunction("postValue") to CaptureStrategy.ValueArgument(),
        )
    },
    // kotlinx.coroutines.flow
    valueContainer(classId("kotlinx.coroutines.flow", "MutableStateFlow")) {
        getter = memberPropertyGetter("value")
        setter = listOf(memberPropertySetter("value"))
        captureTargets = listOf(
            memberPropertySetter("value") to CaptureStrategy.ValueArgument(),
            memberFunction("update") to CaptureStrategy.LambdaLastExpression(),
            memberFunction("updateAndGet") to CaptureStrategy.LambdaLastExpression(),
            memberFunction("getAndUpdate") to CaptureStrategy.LambdaLastExpression(),
            memberFunction("emit") to CaptureStrategy.ValueArgument(),
            memberFunction("tryEmit") to CaptureStrategy.ValueArgument(),
        )
    },
    // kotlin/collections
    selfContainedValueContainer(classId("kotlin.collections", "MutableList")) {
        setter = listOf(
            memberFunction("clear") { emptyValueParameters() },
            memberFunction("addAll") {
                valueParameters {
                    addTypeMatcher(
                        genericParameter(
                            classId("kotlin.collections", "Collection"),
                            typeParameter(0)
                        )
                    )
                }
            },
        )
        captureTargets = listOf(
            memberFunction("add") to CaptureStrategy.AfterCall,
            memberFunction("addAll") to CaptureStrategy.AfterCall,
            memberFunction("clear") { emptyValueParameters() } to CaptureStrategy.AfterCall,
            memberFunction("remove") to CaptureStrategy.AfterCall,
            memberFunction("removeAll") to CaptureStrategy.AfterCall,
            memberFunction("removeAt") to CaptureStrategy.AfterCall,
            memberFunction("replaceAll") to CaptureStrategy.AfterCall,
            function(CallableId(FqName("kotlin.collections"), Name.identifier("set"))) to CaptureStrategy.AfterCall,
        )
    },
    selfContainedValueContainer(classId("kotlin.collections", "MutableSet")) {
        setter = listOf(
            memberFunction("clear") { emptyValueParameters() },
            memberFunction("addAll") {
                valueParameters {
                    addTypeMatcher(
                        genericParameter(
                            classId("kotlin.collections", "Collection"),
                            typeParameter(0)
                        )
                    )
                }
            },
        )
        captureTargets = listOf(
            memberFunction("add") to CaptureStrategy.AfterCall,
            memberFunction("addAll") to CaptureStrategy.AfterCall,
            memberFunction("clear") { emptyValueParameters() } to CaptureStrategy.AfterCall,
            memberFunction("remove") to CaptureStrategy.AfterCall,
            memberFunction("removeAll") to CaptureStrategy.AfterCall,
        )
    },
    selfContainedValueContainer(classId("kotlin.collections", "MutableMap")) {
        setter = listOf(
            memberFunction("clear") { emptyValueParameters() },
            memberFunction("putAll"),
        )
        captureTargets = listOf(
            memberFunction("clear") { emptyValueParameters() } to CaptureStrategy.AfterCall,
            memberFunction("put") to CaptureStrategy.AfterCall,
            memberFunction("putAll") to CaptureStrategy.AfterCall,
            memberFunction("remove") to CaptureStrategy.AfterCall,
            memberFunction("replace") to CaptureStrategy.AfterCall,
            memberFunction("replaceAll") to CaptureStrategy.AfterCall,
            function(CallableId(FqName("kotlin.collections"), Name.identifier("set"))) to CaptureStrategy.AfterCall,
        )
    },
    // androidx.compose.runtime
    valueContainer(classId("androidx.compose.runtime", "MutableState")) {
        getter = memberPropertyGetter("value")
        setter = listOf(memberPropertySetter("value"))
        captureTargets = listOf(
            memberPropertySetter("value") to CaptureStrategy.ValueArgument(),
        )
    },
    valueContainer(classId("androidx.compose.runtime", "MutableIntState")) {
        getter = memberPropertyGetter("value")
        setter = listOf(memberPropertySetter("value"))
        captureTargets = listOf(
            memberPropertySetter("value") to CaptureStrategy.ValueArgument(),
            memberPropertySetter("intValue") to CaptureStrategy.ValueArgument(),
        )
        serializeAs = classId("kotlin", "Int")
    },
    valueContainer(classId("androidx.compose.runtime", "MutableLongState")) {
        getter = memberPropertyGetter("value")
        setter = listOf(memberPropertySetter("value"))
        captureTargets = listOf(
            memberPropertySetter("value") to CaptureStrategy.ValueArgument(),
            memberPropertySetter("longValue") to CaptureStrategy.ValueArgument(),
        )
        serializeAs = classId("kotlin", "Long")
    },
    valueContainer(classId("androidx.compose.runtime", "MutableFloatState")) {
        getter = memberPropertyGetter("value")
        setter = listOf(memberPropertySetter("value"))
        captureTargets = listOf(
            memberPropertySetter("value") to CaptureStrategy.ValueArgument(),
            memberPropertySetter("floatValue") to CaptureStrategy.ValueArgument(),
        )
        serializeAs = classId("kotlin", "Float")
    },
    valueContainer(classId("androidx.compose.runtime", "MutableDoubleState")) {
        getter = memberPropertyGetter("value")
        setter = listOf(memberPropertySetter("value"))
        captureTargets = listOf(
            memberPropertySetter("value") to CaptureStrategy.ValueArgument(),
            memberPropertySetter("doubleValue") to CaptureStrategy.ValueArgument(),
        )
        serializeAs = classId("kotlin", "Double")
    },
    selfContainedValueContainer(classId("androidx.compose.runtime.snapshots", "SnapshotStateList")) {
        setter = listOf(
            memberFunction("clear"),
            memberFunction("addAll") {
                valueParameters {
                    addTypeMatcher(
                        genericParameter(
                            classId("kotlin.collections", "Collection"),
                            typeParameter(0)
                        )
                    )
                }
            },
        )
        captureTargets = listOf(
            memberFunction("add") to CaptureStrategy.AfterCall,
            memberFunction("addAll") to CaptureStrategy.AfterCall,
            memberFunction("clear") to CaptureStrategy.AfterCall,
            memberFunction("remove") to CaptureStrategy.AfterCall,
            memberFunction("removeAll") to CaptureStrategy.AfterCall,
            memberFunction("removeAt") to CaptureStrategy.AfterCall,
            memberFunction("set") to CaptureStrategy.AfterCall,
            memberFunction("replaceAll") to CaptureStrategy.AfterCall,
        )
        serializeAs = classId("kotlin.collections", "List")
    },
    selfContainedValueContainer(classId("androidx.compose.runtime.snapshots", "SnapshotStateMap")) {
        setter = listOf(
            memberFunction("clear"),
            memberFunction("putAll"),
        )
        captureTargets = listOf(
            memberFunction("clear") to CaptureStrategy.AfterCall,
            memberFunction("put") to CaptureStrategy.AfterCall,
            memberFunction("putAll") to CaptureStrategy.AfterCall,
            memberFunction("remove") to CaptureStrategy.AfterCall,
            function(CallableId(FqName("kotlin.collections"), Name.identifier("set"))) to CaptureStrategy.AfterCall,
        )
        serializeAs = classId("kotlin.collections", "Map")
    },
)
