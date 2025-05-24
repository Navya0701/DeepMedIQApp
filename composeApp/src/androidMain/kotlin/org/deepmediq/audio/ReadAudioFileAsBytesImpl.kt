package org.deepmediq.audio

import java.io.File

actual suspend fun readAudioFileAsBytes(filePath: String): ByteArray {
    return File(filePath).readBytes()
}