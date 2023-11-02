package com.github.kitakkun.backintime.converter

import com.google.gson.Gson
import kotlin.reflect.KClass

class GsonJSONConverter: BackInTimeJSONConverter {
    override fun serialize(value: Any?): String {
        return Gson().toJson(value)
    }
    override fun deserialize(value: String, valueType: KClass<*>): Any? {
        return Gson().fromJson(value, valueType.java)
    }
}
