package com.example.deepmediq.EntryScreen

import ChatListItem
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
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
import androidx.compose.ui.tooling.preview.Preview
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material.icons.filled.Mic
import androidx.compose.ui.input.pointer.pointerInput
import java.util.Locale
import android.Manifest
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.Brush
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.PermissionStatus
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalConfiguration
import com.example.deepmediq.R
import com.example.deepmediq.roomDatabase.Chat
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ChatScreen() {

    // env variables
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current

    // view model
    val entryScreenViewModel: EntryScreenViewModel = hiltViewModel()



    // stores the chat items in the lazy column
    val databaseChats by entryScreenViewModel.databaseChats.collectAsState(initial = emptyList())    // stores the message in the input field
    var message by remember { mutableStateOf("") }
    var displayQuestion by remember {mutableStateOf("")}
    // watches the response from the backend
    val backendResponse = entryScreenViewModel.backendResponse.collectAsState()
    // used to store the state of the lazy column
    val listState = rememberLazyListState()
    // used to scroll to the selected item in the drawer
    var selectedItemIndex = entryScreenViewModel.selectedItem.collectAsState(0)
    // used to show the spinner
    val isLoading = entryScreenViewModel.isLoading.collectAsState(initial = false)
    // used to switch bw the chat screen and the initial screen
    val firstLoad = entryScreenViewModel.firstLoad.collectAsState()
    // used to show the slow load fade in transition during output display
    var slowLoad = remember { mutableStateOf(false) }
    // autoscroll feature
    var shouldAutoScroll by remember { mutableStateOf(true) }



    // Speech Section....


    // permission state
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
                            Toast.makeText(
                                context,
                                "Could not recognize speech",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onResults(results: Bundle?) {
                        val matches =
                            results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (!matches.isNullOrEmpty()) {
                            message += " " + matches[0]
                            Log.d("messageis ", message)
                        }
                        isListening = false
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches =
                            partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
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
            Toast.makeText(
                context,
                "Speech recognition not available on this device",
                Toast.LENGTH_SHORT
            ).show()
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
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
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
                Toast.makeText(context, "Could not start speech recognition", Toast.LENGTH_SHORT)
                    .show()
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



    LaunchedEffect(databaseChats.lastOrNull()?.id) {
        if (shouldAutoScroll && databaseChats.isNotEmpty()) {
            listState.scrollToItem(databaseChats.lastIndex)
        }
    }

    // this runs every time we get a response from the backend
    LaunchedEffect(backendResponse.value) {  // Fix: React to response updates
        if (!backendResponse.value.answer.equals("") && !backendResponse.value.input.equals("Initial input")) {

            // inserts into the database
            entryScreenViewModel.onEvent(ChatGenEvent.OnAddChatGenClick(backendResponse.value))
            slowLoad.value = true
        }
    }

    // this run every time the user clicks on the history items
    LaunchedEffect(selectedItemIndex.value) {
        val index = selectedItemIndex.value
        if (index != -1) if (index != null) {
            listState.animateScrollToItem(if(index-1>0)index-1 else 0)
        }
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
                                Toast.makeText(
                                    context,
                                    "Thank you for your feedback!",
                                    Toast.LENGTH_SHORT
                                ).show()
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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .border(1.dp, Color.Gray, RoundedCornerShape(5.dp))
                            .background(Color.White)
                            .padding(16.dp) // inner padding
                            .clickable {

                                displayQuestion = question
                                entryScreenViewModel.setFirstLoadTrue()
                                shouldAutoScroll = true // Reset scroll behavior
                                scope.launch {
                                    entryScreenViewModel.getBackendResponse(question)
                                }

                            }
                    ) {
                        Text(
                            text = question,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                }
            }

        }// Replace your isLoading handling with:
        else if (isLoading.value) {

                Column {
                    if (databaseChats.isNotEmpty()) {
                        ChatItemBox(
                            input = displayQuestion,
                            output = "Thinking...",
                            isSlow = false
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            color = Color(0xFF87CEEB)
                        )
                    }
                }

        }
        else {
            // Chat messages list
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 72.dp)// Ensure space for the input box
                    .pointerInput(Unit) {
                        detectVerticalDragGestures { _, dragAmount ->
                            if (abs(dragAmount) > 0f) {
                                shouldAutoScroll = false
                            }
                        }
                    },
                state = listState
            ) {

                itemsIndexed(databaseChats) { index,item ->

                     if (slowLoad.value && index == databaseChats.lastIndex) {
                        var visible by remember { mutableStateOf(false) }
                         var animationComplete by remember { mutableStateOf(false) }

                        LaunchedEffect(Unit) {


                            delay(300) // small delay before starting animation
                            visible = true
                            animationComplete = true
                        }

                        val alpha by animateFloatAsState(
                            targetValue = if (visible) 1f else 0f,
                            animationSpec = tween(durationMillis = 1000) // Reduced duration
                        )

                        // Reset slowLoad after animation completes
//                         LaunchedEffect(animationComplete) {
//                             if (animationComplete && shouldAutoScroll) {
//                                 listState.animateScrollToItem(index)
//                                 slowLoad.value = false
//                             }
//                         }

                        Box(modifier = Modifier.alpha(alpha)) {
                            ChatItemBox(
                                input = item.input ?: "",
                                output = item.output ?: "",
                                isSlow = false,
                                alpha = alpha
                            )
                        }
                    }
                    else {
                        ChatItemBox(
                            input = item.input ?: "",
                            output = item.output ?: "",
                            false
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            IconButton(
                                onClick = { showFeedbackDialog = true },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_like),
                                    contentDescription = "Like",
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            IconButton(
                                onClick = { showFeedbackDialog = true },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_dislike),
                                    contentDescription = "Dislike",
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            IconButton(
                                onClick = {
                                    val clipboard =
                                        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Response", item.output)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(
                                        context,
                                        "Copied to clipboard",
                                        Toast.LENGTH_SHORT
                                    )
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
                    Spacer(modifier = Modifier.padding(3.dp))
                    Log.d("last","entered")
                }
                // Show loading indicator if waiting for a response

                val screenHeight = configuration.screenHeightDp.dp

                if (isLoading.value) {
                    item {
                        Spacer(modifier = Modifier.height(screenHeight * 0.70f))
                    }
                }

            }
        }


        // Input field and send button (Always visible)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding(), // Adjust for keyboard,
            verticalArrangement = Arrangement.Bottom
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
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
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 16.sp,
                            color = Color.Black
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = {
                            if (message.isNotBlank()) {
                                displayQuestion = message
                                shouldAutoScroll = true
                                entryScreenViewModel.getBackendResponse(message)
                                message = ""
                                entryScreenViewModel.setFirstLoadTrue()
                            }
                        }),
                        decorationBox = { innerTextField ->
                            if (message.isEmpty()) {
                                Text(
                                    "How can DeepMedIQ help you today...",
                                    color = Color.Gray,
                                    fontSize = 16.sp
                                )
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
                                    displayQuestion = message
                                    shouldAutoScroll = true
                                    entryScreenViewModel.getBackendResponse(message)
                                    message = ""
                                    entryScreenViewModel.setFirstLoadTrue()
                                }
                            }
                    )
                }
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
fun ChatItemBox(input: String, output: String,isSlow: Boolean,alpha:Float = 1f) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.White, Color.White)
                ),
                RoundedCornerShape(16.dp)
            )
    ) {
        Column {
            ChatBubble(text = "$input", color = Color(0xFFFFFFFF
            ), textColor = Color.Black) // Soft Blue
            Spacer(modifier = Modifier.height(8.dp))
            GptResponse(text = "$output", color = Color(0xFFF5F7F7
            ), textColor = Color.Black,isSlow,alpha) // Deep Slate Blue
        }
    }

}

@Composable
fun ChatBubble(text: String, color: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color, RoundedCornerShape(12.dp))
            .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
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
fun GptResponse(text: String, color: Color, textColor: Color,isSlow:Boolean,alpha: Float){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color, RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFFcccccc), RoundedCornerShape(12.dp))
            .padding(12.dp)


    ) {
        if(isSlow==true){
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.ic_send),
                    contentDescription = null,
                    modifier = Modifier
                        .size(20.dp) // Small like a dot
                        .alignByBaseline() // Align with text baseline to reduce vertical space
                )
                Spacer(modifier = Modifier.width(6.dp)) // Optional spacing between dot and text
                var delayMillis: Long = 50L
                var visibleText by remember { mutableStateOf("") }

                LaunchedEffect(text) {
                    visibleText = ""
                    text.forEachIndexed { index, _ ->
                        visibleText = text.substring(0, index + 1)
                        delay(delayMillis)
                    }
                }
                Text(
                    text = visibleText,
                    color = textColor,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Start,

                )
            }
        }else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.ic_send),
                    contentDescription = null,
                    modifier = Modifier
                        .size(20.dp) // Small like a dot
                        .alignByBaseline() // Align with text baseline to reduce vertical space
                )
                Spacer(modifier = Modifier.width(6.dp)) // Optional spacing between dot and text
                Text(
                    text = text,
                    color = textColor,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.alpha(alpha)
                )
            }
        }
    }
}