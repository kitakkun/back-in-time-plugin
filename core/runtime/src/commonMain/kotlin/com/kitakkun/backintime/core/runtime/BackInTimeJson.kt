package com.kitakkun.backintime.core.runtime

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

/**
 * This json value is used internally for encoding or decoding value.
 */
@OptIn(ExperimentalSerializationApi::class)
@Suppress("unused")
val backInTimeJson = Json {
    encodeDefaults = true
    explicitNulls = true
}
