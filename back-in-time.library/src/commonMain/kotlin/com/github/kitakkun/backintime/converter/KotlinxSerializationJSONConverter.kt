package com.github.kitakkun.backintime.converter

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.reflect.KClass

class KotlinxSerializationJSONConverter: BackInTimeJSONConverter {
    override fun serialize(value: Any?): String {
        return Json.encodeToString(value)
    }
    override fun deserialize(value: String, valueType: KClass<*>): Any? {
        return Json.decodeFromString(value)
    }
}
