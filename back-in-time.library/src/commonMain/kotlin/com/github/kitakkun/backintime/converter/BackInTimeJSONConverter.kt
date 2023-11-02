package com.github.kitakkun.backintime.converter

import kotlin.reflect.KClass

interface BackInTimeJSONConverter {
    fun serialize(value: Any?): String
    fun deserialize(value: String, valueType: KClass<*>): Any?
}
