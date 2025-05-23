package org.deepmediq.chat.presentation.chat_screen

import androidx.compose.foundation.background
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
import kotlinx.coroutines.delay
import org.deepmediq.chat.domain.Chat
import org.deepmediq.chat.presentation.chat_screen.components.ChatList
import org.deepmediq.chat.presentation.chat_screen.components.ChatSearchBar
import org.deepmediq.core.presentation.DarkBlue
import org.deepmediq.core.presentation.DesertWhite

import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

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
            .background(DarkBlue)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
                .widthIn(max = 400.dp)
                .fillMaxWidth()
                .padding(16.dp)
        )
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
                ChatList(
                    state.searchResults,
                    state,
                    {},
                    scrollState = scrollState
                )
            }
        }
    }
}