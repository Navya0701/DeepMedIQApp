package org.deepmediq.audio

import android.app.Activity
import org.deepmediq.MainActivity

lateinit var permissionActivityProvider: () -> Activity

actual fun requestAudioPermission(onGranted: () -> Unit) {
    val activity = permissionActivityProvider() as? MainActivity
    activity?.let {
        it.runOnUiThread {
            it.checkAndRequestAudioPermission(onGranted)
        }
    }
}
