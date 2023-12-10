package com.github.kitakkun.backintime.runtime

import kotlinx.serialization.json.Json

/**
 * This json value is used internally for encoding or decoding value.
 */
@Suppress("unused")
val backInTimeJson = Json { encodeDefaults = true }
