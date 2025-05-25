package com.plcoding.bookpedia.book.presentation.chat_screen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.dp
import com.plcoding.bookpedia.book.domain.Chat
import com.plcoding.bookpedia.book.presentation.chat_screen.ChatScreenState

@Composable
fun ChatList(
    chats: List<Chat>,
    state: ChatScreenState,
    onBookClick: (Chat) -> Unit,
    modifier: Modifier = Modifier,
    scrollState: LazyListState = rememberLazyListState()
) {
    // State to control last item animation
    var animateLastItem by remember { mutableStateOf(false) }

    // LaunchedEffect to handle scrolling and animation
    LaunchedEffect(chats.size) {  // Watch the size of chats instead of searchResults
        if (chats.isNotEmpty()) {
            // Add a small delay to ensure the list is populated
            kotlinx.coroutines.delay(100)
            scrollState.animateScrollToItem(chats.lastIndex)
        }
    }

    // Handle loading state
    if (state.isLoading) {
        ChatListItem(
            chat = Chat("0", state.searchQuery, "Thinking...", ""),
            onClick = {}
        )
        CircularProgressIndicator(modifier = Modifier.size(16.dp))
    } else {
        LazyColumn(
            modifier = modifier,
            state = scrollState,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(chats, key = { chat -> chat.id }) { chat ->
                ChatListItem(
                    chat = chat,
                    modifier = Modifier
                        .widthIn(max = 700.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    onClick = { onBookClick(chat) }
                )
            }
        }
    }
}

