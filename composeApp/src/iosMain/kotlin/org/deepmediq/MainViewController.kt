package org.deepmediq

import androidx.compose.ui.window.ComposeUIViewController
import org.deepmediq.app.App
import org.deepmediq.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) { App() }