package com.kitakkun.backintime.compiler.yaml

import kotlinx.serialization.Serializable

@Serializable
data class BackInTimeYamlConfiguration(val trackableStateHolders: List<TrackableStateHolder>)
