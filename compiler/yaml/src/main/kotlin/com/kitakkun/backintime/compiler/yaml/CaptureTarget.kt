package com.kitakkun.backintime.compiler.yaml

import kotlinx.serialization.Serializable

@Serializable
data class CaptureTarget(
    val signature: CallableSignature,
    val strategy: CaptureStrategy,
)
