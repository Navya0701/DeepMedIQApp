package org.deepmediq.app

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.LocalPlatformContext
import deepmediq.composeapp.generated.resources.Res
import deepmediq.composeapp.generated.resources.ic_app_logo
import kotlinx.coroutines.launch
import org.deepmediq.chat.presentation.chat_screen.ChatScreenRoot
import org.deepmediq.chat.presentation.chat_screen.ChatScreenState
import org.deepmediq.chat.presentation.chat_screen.ChatScreenViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App(
    viewModel: ChatScreenViewModel = koinViewModel(),
) {
    MaterialTheme {
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val state by viewModel.state.collectAsStateWithLifecycle(
            initialValue = ChatScreenState(
                searchQuery = "Hi how may i help you",
                searchResults = emptyList(),
                favouriteChats = emptyList(),
                isLoading = true
            )
        )
        val dbChats = state.searchResults
        val scope = rememberCoroutineScope()

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = Color(0xFF706c6c),
                    modifier = Modifier
                        .width(280.dp)
                        .clip(
                            RoundedCornerShape(
                                topEnd = 16.dp,
                                bottomEnd = 0.dp
                            )
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 0.dp, bottom = 10.dp)
                            .background(Color.White, shape = RoundedCornerShape(12.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(resource = Res.drawable.ic_app_logo),
                            contentDescription = "App Logo",
                            modifier = Modifier.size(120.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                    Box {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Black, shape = RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.Black, shape = RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "History (${dbChats.size} items)",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )

                                    LazyColumn(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f)
                                    ) {
                                        items(dbChats) { item ->
                                            ChatItemBox(
                                                id = item.id.toString(),
                                                input = item.question ?: "",
                                                output = item.response ?: "",
                                                onClick = {
                                                    scope.launch { drawerState.close() }
                                                },
                                                timeStamp = item.id
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, shape = RoundedCornerShape(12.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "This is empty ",
                            color = Color.Black
                        )
                    }
                }
            }
        ) {
            // Use a Box to control the entire layout and ignore keyboard insets
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Image(
                                        painter = painterResource(resource = Res.drawable.ic_app_logo),
                                        contentDescription = "App Logo",
                                        modifier = Modifier
                                            .size(150.dp)
                                            .clickable { },
                                        contentScale = ContentScale.Fit
                                    )
                                }
                            },
                            navigationIcon = {
                                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                    Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                                }
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = Color.White,
                                navigationIconContentColor = Color.Black,
                                titleContentColor = Color.Black
                            )
                        )
                    },
                    contentWindowInsets = WindowInsets(0.dp) // Disable insets for Scaffold
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        ChatScreenRoot()
                    }
                }
            }
        }
    }
}

@Composable
fun ChatItemBox(
    id: String,
    input: String,
    output: String,
    timeStamp: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(2.dp, Color.Gray, RoundedCornerShape(12.dp))
            .padding(12.dp)
            .clickable { onClick() }
    ) {
        Column {
            Text(
                text = " $input",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = timeStamp,
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}