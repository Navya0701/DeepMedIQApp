package com.example.deepmediq.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx. compose. material3.Typography
import androidx.compose.ui.graphics.Color

@Composable
fun DeepMedIQTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color.White,
            background = Color.White,
            surface = Color.White,
            onPrimary = Color.Black,
            onBackground = Color.Black,
            onSurface = Color.Black
        ),
        typography = MaterialTheme.typography,
        shapes = MaterialTheme.shapes,
        content = content
    )
}

