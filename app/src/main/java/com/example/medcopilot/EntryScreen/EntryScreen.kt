package com.example.medcopilot.EntryScreen

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import com.example.medcopilot.util.GoogleSignInUtils

@Composable
 fun ChatScreen() {
     // login





    val entryScreenViewModel: EntryScreenViewModel = hiltViewModel()
    var message by remember { mutableStateOf("") }
    val response = entryScreenViewModel.chatMessages.collectAsState()  // Corrected variable
    var messages by remember { mutableStateOf(listOf("Hello!", "How can I assist you today?", "Tell me more.")) }

    val listState = rememberLazyListState()  // State for scrolling

    // Corrected variable


    LaunchedEffect(response.value) {  // Fix: React to response updates
        Log.d("ChatScreen", "Response: ${response.value}")
        if (!response.value.answer.equals("") && !response.value.input.equals("Initial input")) {
            entryScreenViewModel.onEvent(ChatGenEvent.OnAddChatGenClick(response.value))
            messages = messages + response.value.answer
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {


        LazyColumn(modifier = Modifier.weight(1f)) {
            items(messages) { msg ->
                Text(
                    text = msg,
                    color = Color.Black,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(8.dp)
                        .border(2.dp, Color.Green, shape = RoundedCornerShape(12.dp))
                        .background(Color.White, shape = RoundedCornerShape(12.dp))
                        .shadow(4.dp, shape = RoundedCornerShape(12.dp))
                        .padding(16.dp)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(Color.LightGray, shape = RoundedCornerShape(12.dp))
                .padding(8.dp)
        ) {
            BasicTextField(
                value = message,
                onValueChange = { message = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
                    .border(2.dp, Color.Red, shape = RoundedCornerShape(12.dp))
                    .background(Color.White, shape = RoundedCornerShape(12.dp))
                    .padding(16.dp),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    if (message.isNotBlank()) {
                        messages = messages + message
                        message = ""
                    }
                })
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (message.isNotBlank()) {
                        Log.d("ChatScreen", "Sending message: $message")
                        messages += message
                        message = ""
                        entryScreenViewModel.getChatMessages(message)
                    }
                },
                modifier = Modifier
                    .padding(8.dp)
                    .shadow(4.dp, shape = RoundedCornerShape(12.dp))
            ) {
                Text("Send")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    ChatScreen()
}