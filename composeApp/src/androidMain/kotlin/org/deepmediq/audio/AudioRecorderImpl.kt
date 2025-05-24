package org.deepmediq.audio

import android.media.MediaRecorder
import android.util.Log
import java.io.File
import org.deepmediq.audio.permissionActivityProvider

actual class AudioRecorder actual constructor(private val callback: AudioDataCallback) {
    private var recorder: MediaRecorder? = null
    private var outputFile: String? = null

    actual fun startRecording() {
        val activity = try { permissionActivityProvider() } catch (_: Exception) { null }
        val dir = activity?.getExternalFilesDir(null) ?: activity?.filesDir
        outputFile = File(dir, "recording_${System.currentTimeMillis()}.m4a").absolutePath
        Log.d("AudioRecorder", "Start recording to: $outputFile")
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(outputFile)
            try {
                prepare()
                start()
                Log.d("AudioRecorder", "Recording started")
            } catch (e: Exception) {
                Log.e("AudioRecorder", "Error starting recording", e)
                callback.onError(e)
            }
        }
    }

    actual fun stopRecording() {
        try {
            recorder?.apply {
                stop()
                release()
            }
            recorder = null
            Log.d("AudioRecorder", "Recording stopped. File: $outputFile")
            callback.onRecordingFinished(outputFile)
        } catch (e: Exception) {
            Log.e("AudioRecorder", "Error stopping recording", e)
            callback.onError(e)
        }
    }

}