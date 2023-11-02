package com.github.kitakkun.backintime.runtime

import com.github.kitakkun.backintime.converter.BackInTimeJSONConverter
import kotlin.reflect.KClass

class BackInTimeDefaultJSONConverter : BackInTimeJSONConverter {
    override fun serialize(value: Any?): String {
        return value.toString()
    }

    override fun deserialize(value: String, valueType: KClass<*>): Any? {
        return when (valueType) {
            String::class -> value
            Int::class -> value.toIntOrNull()
            Long::class -> value.toLongOrNull()
            Float::class -> value.toFloatOrNull()
            Double::class -> value.toDoubleOrNull()
            Boolean::class -> value.toBooleanStrictOrNull()
            else -> null
        }
    }
}
