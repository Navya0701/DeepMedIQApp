package org.deepmediq.audio

expect class AudioRecorder(callback: AudioDataCallback) {
    fun stopRecording()
    fun startRecording()
}