package com.kitakkun.backintime.compiler.backend.valuecontainer

import com.kitakkun.backintime.compiler.backend.valuecontainer.match.function
import com.kitakkun.backintime.compiler.backend.valuecontainer.match.memberFunction
import com.kitakkun.backintime.compiler.backend.valuecontainer.raw.CaptureStrategy
import com.kitakkun.backintime.compiler.backend.valuecontainer.raw.selfContainedValueContainer
import org.jetbrains.kotlin.javac.resolve.classId
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

val ValueContainerBuiltIns = listOf(
    // kotlin/collections
    selfContainedValueContainer(classId("kotlin.collections", "MutableList")) {
        setter = listOf(
            memberFunction("clear") { emptyValueParameters() },
            memberFunction("addAll") {
                valueParameters {
                    addTypeMatcher(
                        genericParameter(
                            classId("kotlin.collections", "Collection"),
                            typeParameter(0),
                        ),
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
                            typeParameter(0),
                        ),
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
    selfContainedValueContainer(classId("androidx.compose.runtime.snapshots", "SnapshotStateList")) {
        setter = listOf(
            memberFunction("clear"),
            memberFunction("addAll") {
                valueParameters {
                    addTypeMatcher(
                        genericParameter(
                            classId("kotlin.collections", "Collection"),
                            typeParameter(0),
                        ),
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
