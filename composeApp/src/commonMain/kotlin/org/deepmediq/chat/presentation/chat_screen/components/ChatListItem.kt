package org.deepmediq.chat.presentation.chat_screen.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.rememberAsyncImagePainter
import kotlinx.coroutines.delay
import org.deepmediq.chat.domain.Chat

import org.jetbrains.compose.resources.painterResource
import kotlin.math.round
import org.deepmediq.core.presentation.LightBlue

@Composable
fun ChatListItem(
    chat: Chat,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    animationDurationMs: Int =3000, // Default 1-second animation
    animationDelayMs: Int = 0 // Default no delay
) {
    // State to trigger animation
    var startAnimation by remember { mutableStateOf(false) }

    // Animate alpha (fade-in)
    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = animationDurationMs, delayMillis = animationDelayMs),
        label = "textAlpha"
    )

    // Animate scale (scale-in from 0.8 to 1)
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.8f,
        animationSpec = tween(durationMillis = animationDurationMs, delayMillis = animationDelayMs),
        label = "textScale"
    )

    // Start animation when composable is first composed or chat changes
    LaunchedEffect(chat.id) {
        startAnimation = false // Reset to ensure animation retriggers
        delay(animationDelayMs.toLong()) // Respect delay
        startAnimation = true
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Question: ${chat.question}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .alpha(alpha)
                    .graphicsLayer { scaleX = scale; scaleY = scale }
            )
            Text(
                text = "Response: ${chat.response}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .alpha(alpha)
                    .graphicsLayer { scaleX = scale; scaleY = scale }
            )
        }
    }
}