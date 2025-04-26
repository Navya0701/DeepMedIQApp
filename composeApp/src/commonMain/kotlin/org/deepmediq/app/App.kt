package org.deepmediq.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import org.deepmediq.chat.presentation.chat_screen.ChatScreenRoot
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
            ChatScreenRoot ()
        }
    }