package org.deepmediq.chat.presentation.chat_screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import org.deepmediq.chat.domain.Chat
import org.deepmediq.chat.presentation.chat_screen.components.ChatList
import org.deepmediq.chat.presentation.chat_screen.components.ChatSearchBar
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChatScreenRoot(
    modifier: Modifier = Modifier,
    viewModel: ChatScreenViewModel = koinViewModel(),
    onBookClick: (Chat) -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle(
        initialValue = ChatScreenState(
            searchQuery = "Hi how may i help you",
            searchResults = emptyList(),
            favouriteChats = emptyList(),
            isLoading = true
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
    ) {
        // ChatList with weight to take available space
        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            color = Color.White,
            shape = RoundedCornerShape(
                topStart = 32.dp,
                topEnd = 32.dp
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ChatList(
                    chats = state.searchResults,
                    state = state,
                    onBookClick = {},
                    scrollState = scrollState
                )
            }
        }

        // ChatSearchBar at the bottom with imePadding
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
            modifier = Modifier
                .fillMaxWidth()
                .imePadding() // Adjust for keyboard
                .padding(16.dp)
                .widthIn(min = 200.dp, max = 600.dp)
        )
    }
}