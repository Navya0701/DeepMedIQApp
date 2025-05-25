package com.plcoding.bookpedia.book.presentation.chat_screen.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cmp_bookpedia.composeapp.generated.resources.Res
import cmp_bookpedia.composeapp.generated.resources.ic_send

import kotlinx.coroutines.delay
import com.plcoding.bookpedia.book.domain.Chat
import org.jetbrains.compose.resources.painterResource

@Composable
fun ChatListItem(
    chat: Chat,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    animationDurationMs: Int = 3000,
    animationDelayMs: Int = 0
) {
    var startAnimation by remember { mutableStateOf(false) }

    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = animationDurationMs, delayMillis = animationDelayMs),
        label = "textAlpha"
    )

    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.8f,
        animationSpec = tween(durationMillis = animationDurationMs, delayMillis = animationDelayMs),
        label = "textScale"
    )

    LaunchedEffect(chat.id) {
        startAnimation = false
        delay(animationDelayMs.toLong())
        startAnimation = true
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(16.dp)
        ) {
            // QUESTION
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = chat.question,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Normal,
                        lineBreak = LineBreak.Paragraph,
                        lineHeight = 20.sp
                    ),
                    textAlign = TextAlign.Start,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Clip,
                    softWrap = true,
                    modifier = Modifier
                        .alpha(alpha)
                        .graphicsLayer { scaleX = scale; scaleY = scale }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // RESPONSE
            Row(verticalAlignment = Alignment.CenterVertically){
                Image(
                    painter = painterResource(Res.drawable.ic_send),
                    contentDescription = null,
                    modifier = Modifier
                        .size(20.dp) // Small like a dot
                        .alignByBaseline() // Align with text baseline to reduce vertical space
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = formatResponse(chat.response),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Normal,
                            lineBreak = LineBreak.Paragraph,
                            lineHeight = 20.sp
                        ),
                        textAlign = TextAlign.Start,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Clip,
                        softWrap = true,
                        modifier = Modifier
                            .alpha(alpha)
                            .graphicsLayer { scaleX = scale; scaleY = scale }
                    )
                }
            }

        }
    }
}


@Composable
fun formatResponse(text: String): AnnotatedString {
    return buildAnnotatedString {
        var i = 0
        while (i < text.length) {
            if (text[i] == '(') {
                i++
                val start = length
                while (i < text.length && text[i] != ')') {
                    append(text[i])
                    i++
                }
                val end = length
                addStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize * 1.1f
                    ),
                    start = start,
                    end = end
                )
                if (i < text.length && text[i] == ')') {
                    i++ // skip closing bracket
                }
            } else {
                append(text[i])
                i++
            }
        }
    }
}