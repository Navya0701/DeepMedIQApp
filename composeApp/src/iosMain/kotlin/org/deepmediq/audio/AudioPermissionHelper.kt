package org.deepmediq.audio

actual fun requestAudioPermission(onGranted: () -> Unit) {
    // iOS: implement permission logic if needed, or just call onGranted()
    onGranted()
}
