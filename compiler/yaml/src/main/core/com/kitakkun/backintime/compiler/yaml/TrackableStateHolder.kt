package com.kitakkun.backintime.compiler.yaml

import kotlinx.serialization.Serializable

@Serializable
data class TrackableStateHolder(
    val classId: String,
    val accessor: StateAccessor,
    val captures: List<CaptureTarget>,
    val serializeAs: TypeSignature,
)
