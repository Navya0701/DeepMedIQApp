package org.deepmediq.chat.data.dto
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

actual fun generateDefaultId(): String {
    return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString()
}