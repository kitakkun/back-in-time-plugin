package io.github.kitakkun.backintime.debugger.featurecommon.util

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter

fun formatEpochSecondsToDateTimeText(seconds: Long): String {
    val instant = Instant.fromEpochSeconds(seconds)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    return formatter.format(localDateTime.toJavaLocalDateTime())
}
