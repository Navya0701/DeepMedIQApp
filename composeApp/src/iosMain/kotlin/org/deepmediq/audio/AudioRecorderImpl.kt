package org.deepmediq.audio

import platform.AVFoundation.*
import platform.Foundation.*
import platform.darwin.NSObject
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreMedia.CMTime
import platform.CoreMedia.CMTimeMake

@OptIn(ExperimentalForeignApi::class)
actual class AudioRecorder actual constructor(private val callback: AudioDataCallback) {
    private var audioRecorder: AVAudioRecorder? = null
    private var outputFile: String? = null
    private var isPermissionGranted = false
    private var isCheckingPermission = false

    private fun checkMicrophonePermission() {
        if (isCheckingPermission) return
        isCheckingPermission = true

        when (AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeAudio)) {
            AVAuthorizationStatus.AVAuthorizationStatusAuthorized -> {
                isPermissionGranted = true
                setupAudioSession()
            }
            AVAuthorizationStatus.AVAuthorizationStatusNotDetermined -> {
                requestMicrophonePermission()
            }
            AVAuthorizationStatus.AVAuthorizationStatusDenied,
            AVAuthorizationStatus.AVAuthorizationStatusRestricted -> {
                callback.onError(Exception("Microphone access denied. Please enable it in Settings."))
            }
        }
        isCheckingPermission = false
    }

    private fun requestMicrophonePermission() {
        AVCaptureDevice.requestAccessForMediaType(AVMediaTypeAudio) { granted ->
            isPermissionGranted = granted
            if (granted) {
                setupAudioSession()
            } else {
                callback.onError(Exception("Microphone access denied"))
            }
        }
    }

    private fun setupAudioSession() {
        val audioSession = AVAudioSession.sharedInstance()
        try {
            audioSession.setCategory(AVAudioSessionCategoryPlayAndRecord)
            audioSession.setMode(AVAudioSessionModeDefault)
            audioSession.setActive(true)
        } catch (e: Exception) {
            callback.onError(Exception("Failed to setup audio session: ${e.message}"))
        }
    }

    actual fun startRecording() {
        checkMicrophonePermission()
        
        if (!isPermissionGranted) {
            callback.onError(Exception("Microphone permission not granted"))
            return
        }

        val audioSession = AVAudioSession.sharedInstance()
        try {
            // Get the documents directory
            val documentsPath = NSHomeDirectory() + "/Documents"
            val fileName = "recording_${System.currentTimeMillis()}.m4a"
            outputFile = "$documentsPath/$fileName"

            // Configure audio settings
            val settings = mapOf(
                AVFormatIDKey to kAudioFormatMPEG4AAC,
                AVSampleRateKey to 44100.0,
                AVNumberOfChannelsKey to 1,
                AVEncoderAudioQualityKey to AVAudioQuality.HIGH.ordinal
            )

            // Create and configure the recorder
            audioRecorder = AVAudioRecorder(outputFile!!, settings.toNSDictionary(), null).apply {
                prepareToRecord()
                if (!record()) {
                    throw Exception("Failed to start recording")
                }
            }
        } catch (e: Exception) {
            callback.onError(Exception("Failed to start recording: ${e.message}"))
        }
    }

    actual fun stopRecording() {
        if (!isPermissionGranted) {
            callback.onError(Exception("Microphone permission not granted"))
            return
        }

        try {
            audioRecorder?.apply {
                if (isRecording) {
                    stop()
                }
                release()
            }
            audioRecorder = null
            
            // Deactivate audio session
            try {
                AVAudioSession.sharedInstance().setActive(false)
            } catch (e: Exception) {
                // Ignore deactivation errors
            }
            
            callback.onRecordingFinished(outputFile)
        } catch (e: Exception) {
            callback.onError(Exception("Failed to stop recording: ${e.message}"))
        }
    }
}