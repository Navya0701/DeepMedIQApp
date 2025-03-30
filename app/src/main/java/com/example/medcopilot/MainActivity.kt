package com.example.medcopilot

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.medcopilot.EntryScreen.ChatScreen
import com.example.medcopilot.EntryScreen.EntryScreenViewModel
import com.example.medcopilot.api.ChatAPI
import com.example.medcopilot.util.GoogleSignInUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var chatAPI: ChatAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MedCopilotApp()
        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedCopilotApp() {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
        GoogleSignInUtils.doGoogleSignIn(
            context = context,
            scope = scope,
            launcher = null,
            login = {
                Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
            }
        )

    }


    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var selectedScreen by remember { mutableStateOf("Chat") }

    val entryScreenViewModel: EntryScreenViewModel = hiltViewModel()
    val dbChats = entryScreenViewModel.chats.collectAsState( emptyList())

//    // Define your drawer items
//    val menuItems = listOf("Chat", "Profile", "Settings", "About")

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(280.dp)
            ) {
                Text(
                    "MedCopilot",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(16.dp)
                )

                // Drawer items
                dbChats.value.forEach() { item ->
                    ChatItemBox(id = item.id.toString(), input = item.input?:"", output = item.output?:"")
                }

            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(selectedScreen) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        Spacer(modifier = Modifier.width(16.dp)) // Add padding between title and button
                        Button(
                            onClick = {
                                GoogleSignInUtils.doGoogleSignIn(
                                    context = context,
                                    scope = scope,
                                    launcher = launcher,
                                    login = {
                                        Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        ) {
                            Text("Login")
                        }
                    }
                )
            }

        ) { innerPadding ->
            // Main content area
            Column(modifier = Modifier.padding(innerPadding)) {

               ChatScreen()

            }
        }
    }
}


// these are displayed in the already searched chat section
@Composable
fun ChatItemBox(id: String, input: String, output: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(2.dp, Color.Gray, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column {
            Text(text = "ID: $id", color = Color.Blue, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Input: $input", color = Color.Black, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewChatItemBox() {
    ChatItemBox(id = "123", input = "What is insomnia?", output = "Insomnia is a sleep disorder...")
}
