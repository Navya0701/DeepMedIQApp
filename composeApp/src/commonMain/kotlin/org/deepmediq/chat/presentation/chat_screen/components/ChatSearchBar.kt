package org.deepmediq.chat.presentation.chat_screen.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.key.Key.Companion.R
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import deepmediq.composeapp.generated.resources.Res
import deepmediq.composeapp.generated.resources.close_hint
import deepmediq.composeapp.generated.resources.ic_send
import deepmediq.composeapp.generated.resources.mic_24px
import deepmediq.composeapp.generated.resources.search_hint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.deepmediq.chat.data.sampleData.SampleMedicalData
import org.deepmediq.chat.presentation.chat_screen.ChatScreenAction
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.deepmediq.audio.requestAudioPermission

@Composable
fun ChatSearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSendClick: (String) -> Unit,
    onImeSearch: () -> Unit,
    modifier: Modifier = Modifier,
    onMicClick: () -> Unit = {},
    isRecording: Boolean = false
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            shape = RoundedCornerShape(100),
            colors = OutlinedTextFieldDefaults.colors(
                cursorColor = Color.Black,
                focusedBorderColor = Color.Black
            ),
            placeholder = {
                Text(
                    text = if (searchQuery.isEmpty()) "How can DeepMedIQ help you today.." else stringResource(Res.string.search_hint)
                )
            },
            singleLine = false,
            maxLines = 3,
            keyboardActions = KeyboardActions(
                onSearch = { onImeSearch() }
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            leadingIcon = {
                IconButton(onClick = {
                    requestAudioPermission {
                        onMicClick()
                    }
                }) {
                    Image(
                        painter = painterResource(resource = Res.drawable.mic_24px),
                        contentDescription = "Mic",
                        modifier = Modifier.size(24.dp),
                        colorFilter = ColorFilter.tint(if (isRecording) Color.Red else Color.Black)
                    )
                }
            },
            trailingIcon = {
                AnimatedVisibility(
                    visible = searchQuery.isNotBlank()
                ) {
                    IconButton(
                        onClick = {
                            onSendClick(searchQuery)
                        }
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.ic_send),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            },
            modifier = Modifier
                .background(
                    shape = RoundedCornerShape(100),
                    color = Color.White
                )
                .fillMaxWidth()
        )
    }
}