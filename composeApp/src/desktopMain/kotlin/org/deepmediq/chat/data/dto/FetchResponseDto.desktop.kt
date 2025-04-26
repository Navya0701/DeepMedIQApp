package org.deepmediq.chat.data.dto

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

actual fun generateDefaultId(): String {
    val formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS")
    return LocalDateTime.now().format(formatter)
}