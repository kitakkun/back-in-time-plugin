package io.github.kitakkun.backintime.debugger.core.database

import androidx.room.TypeConverter
import io.github.kitakkun.backintime.core.websocket.event.BackInTimeDebugServiceEvent
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class StringListConverter {
    @TypeConverter
    fun convert(stringList: List<String>): String {
        return Json.encodeToString(stringList)
    }

    @TypeConverter
    fun convert(string: String): List<String> {
        return Json.decodeFromString(string)
    }
}

class BackInTimeDebugServiceEventConverter {
    @TypeConverter
    fun convert(event: BackInTimeDebugServiceEvent): String {
        return Json.encodeToString(event)
    }

    @TypeConverter
    fun convert(string: String): BackInTimeDebugServiceEvent {
        return Json.decodeFromString(string)
    }
}
