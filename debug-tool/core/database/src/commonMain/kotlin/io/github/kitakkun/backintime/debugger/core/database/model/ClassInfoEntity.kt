package io.github.kitakkun.backintime.debugger.core.database.model

import androidx.room.Entity
import androidx.room.TypeConverter
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Entity(
    tableName = "class_info",
    primaryKeys = ["sessionId", "name"]
)
data class ClassInfoEntity(
    val sessionId: String,
    val name: String,
    val properties: List<PropertyInfoEntity>,
    val superClassName: String?,
)

@Serializable
data class PropertyInfoEntity(
    val name: String,
    val type: String,
    val debuggable: Boolean,
    val backInTimeDebuggable: Boolean,
) {
    class ListConverter {
        @TypeConverter
        fun convertToString(items: List<PropertyInfoEntity>): String {
            return Json.encodeToString(items)
        }

        @TypeConverter
        fun convertToList(value: String): List<PropertyInfoEntity> {
            return Json.decodeFromString(value)
        }
    }
}
