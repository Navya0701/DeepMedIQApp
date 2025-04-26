package org.deepmediq

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.deepmediq.app.App
import org.deepmediq.di.initKoin

fun main() {
    initKoin()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "DeepMedIQ",
        ) {
            App()
        }
    }
}