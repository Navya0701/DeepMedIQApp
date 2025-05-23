package org.deepmediq.chat.presentation.chat_screen.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import kotlinx.coroutines.delay
import org.deepmediq.chat.domain.Chat
import org.deepmediq.chat.presentation.chat_screen.ChatScreenState

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
    LaunchedEffect(state.searchResults){
        scrollState.animateScrollToItem(state.searchResults.lastIndex, scrollOffset = 0)
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
            // Render all items except the last one
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

            // Render the last item with animation
//            if (chats.isNotEmpty()) {
//                item {
//                    AnimatedVisibility(
//                        visible = animateLastItem,
//                        enter = slideInVertically(animationSpec = tween(2000)) + fadeIn(animationSpec = tween(2000))
//                    ) {
//                        ChatListItem(
//                            chat = chats.last(),
//                            modifier = Modifier
//                                .widthIn(max = 700.dp)
//                                .fillMaxWidth()
//                                .padding(horizontal = 16.dp),
//                            onClick = { onBookClick(chats.last()) }
//                        )
//                    }
//                }
//            }
        }
    }
}