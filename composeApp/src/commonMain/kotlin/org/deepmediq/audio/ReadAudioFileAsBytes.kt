package org.deepmediq.audio

expect suspend fun readAudioFileAsBytes(filePath: String): ByteArray