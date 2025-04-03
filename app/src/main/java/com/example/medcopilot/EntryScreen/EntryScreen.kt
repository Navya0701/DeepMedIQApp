package com.example.medcopilot.EntryScreen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.medcopilot.R
import androidx.compose.ui.tooling.preview.Preview
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicNone
import androidx.compose.ui.input.pointer.pointerInput
import java.util.Locale
import android.Manifest
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Send
import androidx.compose.ui.graphics.Brush
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.PermissionStatus
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.Search


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ChatScreen() {



    val entryScreenViewModel: EntryScreenViewModel = hiltViewModel()

    val responses =
        entryScreenViewModel.chats.collectAsState(emptyList())  // Corrected variable // collected from the database

    var message by remember { mutableStateOf("") }
    val response = entryScreenViewModel.chatMessages.collectAsState()
    var messages by remember { mutableStateOf(emptyList<Pair<String, Boolean>>()) } // Boolean to track user/AI messages
    var responseReceived by remember { mutableStateOf(false) } // Hide predefined questions after first response
    val listState = rememberLazyListState()
    val context = LocalContext.current

    var selectedItemIndex = entryScreenViewModel.selectedItem.collectAsState(0)
    val isLoading =
        entryScreenViewModel.isLoading.collectAsState(initial = false) // Track loading state
    // Permission handling

    val firstLoad = entryScreenViewModel.firstLoad.collectAsState()

    val permissionState = rememberPermissionState(permission = Manifest.permission.RECORD_AUDIO)

    // Speech recognition states
    var isListening by remember { mutableStateOf(false) }
    var speechRecognizer by remember { mutableStateOf<SpeechRecognizer?>(null) }
    var speechRecognitionError by remember { mutableStateOf<String?>(null) }

    // Check if speech recognition is available on this device
    val isSpeechRecognitionAvailable = remember {
        SpeechRecognizer.isRecognitionAvailable(context)
    }

    // Initialize Speech Recognizer
    DisposableEffect(Unit) {
        if (isSpeechRecognitionAvailable) {
            try {
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
                speechRecognizer?.setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        speechRecognitionError = null
                    }

                    override fun onBeginningOfSpeech() {
                        // Speech started
                    }

                    override fun onRmsChanged(rmsdB: Float) {
                        // Sound level changed
                    }

                    override fun onBufferReceived(buffer: ByteArray?) {
                        // More sound data received
                    }

                    override fun onEndOfSpeech() {
                        isListening = false
                    }

                    override fun onError(error: Int) {
                        isListening = false

                        // Translate error code to message
                        speechRecognitionError = when (error) {
                            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                            SpeechRecognizer.ERROR_CLIENT -> "Client side error"
                            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                            SpeechRecognizer.ERROR_NETWORK -> "Network error"
                            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                            SpeechRecognizer.ERROR_NO_MATCH -> "No speech detected"
                            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognition service busy"
                            SpeechRecognizer.ERROR_SERVER -> "Server error"
                            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
                            else -> "Unknown speech recognition error"
                        }

                        // Show detailed error only in debug
                        Log.e("SpeechRecognition", "Error: $speechRecognitionError")

                        // Show simplified error to user
                        if (error != SpeechRecognizer.ERROR_NO_MATCH) {
                            Toast.makeText(context, "Could not recognize speech", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onResults(results: Bundle?) {
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (!matches.isNullOrEmpty()) {
                            message += " " + matches[0]
                            Log.d("messageis ",message)
                        }
                        isListening = false
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
//                        if (!matches.isNullOrEmpty()) {
//                            val newPart = matches[0]
//                            if (!message.endsWith(newPart)) { // Avoid excessive appending
//                                message += " $newPart"
//                            }
//                        }
                    }


                    override fun onEvent(eventType: Int, params: Bundle?) {
                        // Event occurred
                    }
                })
            } catch (e: Exception) {
                Log.e("SpeechRecognition", "Error initializing: ${e.message}")
                speechRecognitionError = "Could not initialize speech recognition"
            }
        }

        onDispose {
            speechRecognizer?.destroy()
        }
    }

    // Function to start speech recognition
    fun startSpeechRecognition() {
        if (!isSpeechRecognitionAvailable) {
            Toast.makeText(context, "Speech recognition not available on this device", Toast.LENGTH_SHORT).show()
            return
        }

        // Check and request permission if needed
        if (permissionState.status != PermissionStatus.Granted) {
            permissionState.launchPermissionRequest()
            return
        }

        // Only proceed if we have permission
        if (permissionState.status == PermissionStatus.Granted) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
                putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
                putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, false)
            }

            try {
                speechRecognizer?.cancel() // Cancel any ongoing recognition
                speechRecognizer?.startListening(intent)
                isListening = true
                speechRecognitionError = null
            } catch (e: Exception) {
                Log.e("SpeechRecognition", "Error starting: ${e.message}")
                Toast.makeText(context, "Could not start speech recognition", Toast.LENGTH_SHORT).show()
                isListening = false
            }
        }
    }

    // Function to stop speech recognition
    fun stopSpeechRecognition() {
        if (isListening) {
            try {
                speechRecognizer?.stopListening()
            } catch (e: Exception) {
                Log.e("SpeechRecognition", "Error stopping: ${e.message}")
            }
            isListening = false
        }
    }

    // Feedback dialog state
    var showFeedbackDialog by remember { mutableStateOf(false) }
    var selectedFeedbackOption by remember { mutableStateOf<String?>(null) }
    var feedbackText by remember { mutableStateOf("") }

    LaunchedEffect(response.value) {  // Fix: React to response updates
        if (!response.value.answer.equals("") && !response.value.input.equals("Initial input")) {
            entryScreenViewModel.onEvent(ChatGenEvent.OnAddChatGenClick(response.value))
        }
    }

    LaunchedEffect(selectedItemIndex.value) {
        val index = responses.value.indexOfFirst { it.id == selectedItemIndex.value }
        if (index != -1) listState.animateScrollToItem(index)
    }

    LaunchedEffect(responses.value) {
        listState.animateScrollToItem(if(responses.value.size - 1<0) 0 else responses.value.size - 1)
    }



    // Feedback Dialog
    if (showFeedbackDialog) {
        Dialog(onDismissRequest = { showFeedbackDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Submit Your Feedback",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Radio options
                    val options = listOf(
                        "Great Response !",
                        "Answer is not correct",
                        "Answer needs to be elaborative",
                        "Factually incorrect",
                        "Missing key information",
                        "Inappropriate, unsafe, or biased",
                        "Other"
                    )

                    options.forEach { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { selectedFeedbackOption = option },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedFeedbackOption == option,
                                onClick = { selectedFeedbackOption = option }
                            )
                            Text(text = option)
                        }
                    }

                    // Feedback text field
                    OutlinedTextField(
                        value = feedbackText,
                        onValueChange = { feedbackText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .height(100.dp),
                        placeholder = { Text("Tell us how to improve...") }
                    )

                    // Submit and Close buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                // Just show toast and close dialog
                                Toast.makeText(context, "Thank you for your feedback!", Toast.LENGTH_SHORT).show()
                                showFeedbackDialog = false
                                selectedFeedbackOption = null
                                feedbackText = ""
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                        ) {
                            Text("Submit")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = { showFeedbackDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                        ) {
                            Text("Close")
                        }
                    }
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (!firstLoad.value) {
            val questions = listOf(
                "What is the best modality to screen for Barrett's esophagus?",
                "What causes Crohn's?",
                "What is the treatment for high grade dysplasia?",
                "Which endoscopic procedures are high-risk?"
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                questions.forEach { question ->
                    Button(
                        onClick = {
                            messages = messages + (question to true)
                            entryScreenViewModel.getChatMessages(question)
                            responseReceived = true
                            entryScreenViewModel.setFirstLoadTrue()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .shadow(6.dp, shape = RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp)),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EA))
                    ) {
                        Text(
                            text = question,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            // Chat messages list
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 72.dp), // Ensure space for the input box
                state = listState
            ) {
                items(responses.value) { msg ->
                    ChatItemBox(
                        input = msg.input ?: "",
                        output = msg.output ?: ""
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(
                            onClick = { showFeedbackDialog = true },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ThumbUp,
                                contentDescription = "Like",
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        IconButton(
                            onClick = { showFeedbackDialog = true },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ThumbDown,
                                contentDescription = "Dislike",
                                tint = Color(0xFFF44336),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                val clipboard =
                                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Response", msg.output)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT)
                                    .show()
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ContentCopy,
                                contentDescription = "Copy",
                                tint = Color(0xFF2196F3),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        // Input field and send button (Always visible)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 12.dp) // Margin for lift-off effect
                .shadow(8.dp, RoundedCornerShape(24.dp)) // Official Google-style shadow
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFF1F3F4)) // Light gray background like Google Search
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Search Icon",
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                BasicTextField(
                    value = message,
                    onValueChange = { message = it },
                    modifier = Modifier.weight(1f),
                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp, color = Color.Black),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = {
                        if (message.isNotBlank()) {
                            messages = messages + (message to true)
                            entryScreenViewModel.getChatMessages(message)
                            message = ""
                            responseReceived = true
                            entryScreenViewModel.setFirstLoadTrue()
                        }
                    }),
                    decorationBox = { innerTextField ->
                        if (message.isEmpty()) {
                            Text("Ask me...", color = Color.Gray, fontSize = 16.sp)
                        }
                        innerTextField()
                    }
                )

                if (isLoading.value) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(24.dp)
                            .padding(start = 8.dp)
                    )
                }
                // Voice Search Icon (Mic)
                Icon(
                    imageVector = if (isListening) Icons.Filled.Mic else Icons.Outlined.Mic,
                    contentDescription = "Voice Search",
                    tint = if (isListening) Color.Red else Color.Gray,
                    modifier = Modifier
                        .size(28.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    if (!isListening) startSpeechRecognition()
                                    else stopSpeechRecognition()
                                }
                            )
                        }

                )

                Spacer(modifier = Modifier.width(8.dp))

                // Send Button
                Image(
                    painter = painterResource(id = R.drawable.ic_send),
                    contentDescription = "Send",
                    modifier = Modifier
                        .size(28.dp)
                        .clickable {
                            if (message.isNotBlank()) {
                                messages = messages + (message to true)
                                entryScreenViewModel.getChatMessages(message)
                                message = ""
                                responseReceived = true
                                entryScreenViewModel.setFirstLoadTrue()
                            }
                        }
                )
            }
        }


    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    ChatScreen()
}

@Composable
fun ChatItemBox(input: String, output: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFF3F8FF), Color(0xFFE3ECF9))
                ),
                RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Column {
            ChatBubble(text = "Input: $input", color = Color(0xFF6200EA), textColor = Color.White) // Soft Blue
            Spacer(modifier = Modifier.height(8.dp))
            GptResponse(text = "Output: $output", color = Color.White, textColor = Color.Black) // Deep Slate Blue
        }
    }
}

@Composable
fun ChatBubble(text: String, color: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Start
        )
    }
}

@Composable
fun GptResponse(text: String, color: Color, textColor: Color){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Start
        )

    }
}