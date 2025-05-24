package org.deepmediq.audio

import DeepgramResponse

interface AudioDataCallback {
    fun onAudioData(data: ByteArray)
    fun onTranscriptReceived(response: DeepgramResponse)
    fun onRecordingFinished(filePath: String?)
    fun onError(error: Throwable)
}
