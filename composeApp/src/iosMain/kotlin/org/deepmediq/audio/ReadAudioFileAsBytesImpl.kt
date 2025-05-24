package org.deepmediq.audio

import kotlinx.cinterop.memScoped
import platform.Foundation.*

actual suspend fun readAudioFileAsBytes(filePath: String): ByteArray {
    val data = NSData.dataWithContentsOfFile(filePath) ?: return ByteArray(0)
    val bytes = ByteArray(data.length.toInt())
    memScoped {
        data.getBytes(bytes.refTo(0), data.length)
    }
    return bytes
}