package org.deepmediq.chat.presentation.chat_screen

import DeepgramResponse
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.deepmediq.chat.domain.Chat
import org.deepmediq.chat.presentation.chat_screen.components.ChatList
import org.deepmediq.chat.presentation.chat_screen.components.ChatSearchBar

import org.deepmediq.core.presentation.DesertWhite
import org.deepmediq.core.presentation.DarkBlue


import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.sp
import coil3.util.Logger
import kotlinx.coroutines.launch
import org.deepmediq.audio.AudioRecorder
import org.deepmediq.audio.AudioDataCallback
import org.deepmediq.audio.readAudioFileAsBytes
import org.deepmediq.deepgram.DeepgramClient
import kotlin.math.round


@Composable
fun ChatScreenRoot(
    viewModel: ChatScreenViewModel = koinViewModel(),
    onBookClick: (Chat) -> Unit = {}
) {

    val state by viewModel.state.collectAsStateWithLifecycle(
        initialValue = ChatScreenState(
            searchQuery = "Hi how may i help you",
            searchResults = emptyList(),
            favouriteChats = emptyList(),
            isLoading = true,
        )
    )




    ChatScreen(
        state = state,
        onAction = { action ->
            viewModel.onAction(action)
        }
    )
}

@Composable
fun ChatScreen(
    state: ChatScreenState,
    onAction: (ChatScreenAction) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

     // --- Recording State ---
    var isRecording by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    var audioRecorder by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<AudioRecorder?>(null) }
    val apiKey = "cf9dccc76a3fb206228317fde483a522433880f5" // Replace with your actual key

      // --- Audio Callback ---
    val audioCallback = object : AudioDataCallback {
        override fun onAudioData(data: ByteArray) {}
        override fun onTranscriptReceived(response: DeepgramResponse) {
            TODO("Not yet implemented")
        }

        override fun onRecordingFinished(filePath: String?) {
            if (filePath != null) {
                coroutineScope.launch {
                    try {
                        // Platform-specific: read file as ByteArray
                        val audioBytes = readAudioFileAsBytes(filePath)
                        val client = DeepgramClient(apiKey)
                        val rawResponse = client.transcribeAudio(audioBytes) // returns String
                        val json = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
                        val transcript = json.decodeFromString<DeepgramResponse>(rawResponse)
                        println(transcript.results.channels[0].alternatives[0].transcript)
                        onAction(ChatScreenAction.OnSearchQueryChange(transcript.results.channels[0].alternatives[0].transcript))

                    } catch (e: Exception) {
                        // Handle error (show snackbar, etc.)
                    }
                }
            }
        }
        override fun onError(error: Throwable) {
            // Handle error (show snackbar, etc.)
        }
    }

    fun toggleRecording() {
        if (!isRecording) {
            audioRecorder = AudioRecorder(audioCallback)
            audioRecorder?.startRecording()
            isRecording = true
        } else {
            audioRecorder?.stopRecording()
            isRecording = false
        }
    }

    // Add this LaunchedEffect to handle the transition
    LaunchedEffect(state.isInitialLoad, state.searchResults) {
        if (!state.isInitialLoad && state.searchResults.isNotEmpty()) {
            coroutineScope.launch {
                kotlinx.coroutines.delay(50)
                try {
                    scrollState.scrollToItem(state.searchResults.lastIndex)
                } catch (e: Exception) {
                    // Handle any scrolling errors
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlue)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            color = DesertWhite,
            shape = RoundedCornerShape(
                topStart = 32.dp,
                topEnd = 32.dp
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (state.isInitialLoad) {
                    // Show initial questions
                    Column {
                        state.initialQuestions.forEach { question ->
                            InitialQuestionItem(
                                question = question,
                                onClick = { onAction(ChatScreenAction.OnSendClick(question)) }
                            )
                        }
                    }
                } else {
                    // Show chat list
                    ChatList(
                        state.searchResults,
                        state,
                        {},
                        scrollState = scrollState
                    )
                }
            }
        }
        ChatSearchBar(
            searchQuery = state.searchQuery,
            onSearchQueryChange = {
                onAction(ChatScreenAction.OnSearchQueryChange(it))
            },
            onSendClick = {
                onAction(ChatScreenAction.OnSendClick(it))
            },
            onImeSearch = {
                keyboardController?.hide()
            },
            onMicClick = { toggleRecording() },
            isRecording = isRecording,
            modifier = Modifier
                .widthIn(max = 400.dp)
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}

@Composable
fun InitialQuestionItem(
    question: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth() // Make the Box take the full available width
            .padding(horizontal = 16.dp, vertical = 8.dp) // Outer padding for spacing around the item
            .border( // Add the border
                width = 1.dp, // Specify the border width
                color = Color.Black, // Specify the border color
                shape = MaterialTheme.shapes.medium // Optional: give the border rounded corners, aligning with Material theme
            )
            .clickable { onClick() } // Make the whole bordered area clickable
            .padding(16.dp) // Inner padding between the border and the text
    ) {
        Text(
            text = question,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth() // Ensure text also considers filling width if needed for alignment
        )
    }
}
