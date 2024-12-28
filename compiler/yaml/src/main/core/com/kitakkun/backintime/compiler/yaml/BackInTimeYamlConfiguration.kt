package com.kitakkun.backintime.compiler.yaml

import kotlinx.serialization.Serializable

@Serializable
data class BackInTimeYamlConfiguration(
    val enabled: Boolean = false,
    val trackableStateHolders: List<TrackableStateHolder>,
)
