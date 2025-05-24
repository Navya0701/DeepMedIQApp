package org.deepmediq

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.deepmediq.app.App
import org.deepmediq.audio.permissionActivityProvider

class MainActivity : ComponentActivity() {
    private val RECORD_AUDIO_REQUEST_CODE = 1001
    private var onAudioPermissionGranted: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionActivityProvider = { this }
        setContent {
            App()
        }
    }

    fun checkAndRequestAudioPermission(onGranted: () -> Unit) {
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            onGranted()
        } else {
            onAudioPermissionGranted = onGranted
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), RECORD_AUDIO_REQUEST_CODE)
        }
    }

    @Suppress("DEPRECATION")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, // <-- fix type here
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RECORD_AUDIO_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onAudioPermissionGranted?.invoke()
        }
        onAudioPermissionGranted = null
    }

    // In your mic button click handler, use:
    // checkAndRequestAudioPermission { startRecording() }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}