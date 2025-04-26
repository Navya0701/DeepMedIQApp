package org.deepmediq.chat.data.dto

import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.O)
actual fun generateDefaultId(): String {
    return java.time.LocalDateTime.now().toString()
}