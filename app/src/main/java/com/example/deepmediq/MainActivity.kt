package com.example.deepmediq

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.deepmediq.EntryScreen.ChatScreen
import com.example.deepmediq.EntryScreen.EntryScreenViewModel
import com.example.deepmediq.api.ChatAPI
import com.example.deepmediq.util.GoogleSignInUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import org.vosk.Recognizer
import org.vosk.Model
import org.vosk.android.SpeechService
import org.vosk.android.RecognitionListener
import android.content.res.AssetManager
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import java.io.File
import java.io.FileOutputStream
import com.example.deepmediq.R
import com.example.deepmediq.loginScreen.LoginScreen
import com.example.deepmediq.theme.DeepMedIQTheme
import com.example.deepmediq.util.Routes
import com.example.deepmediq.util.UiEvent
//import com.example.signupScreen.SignupScreen
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable

import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.signupScreen.SignupScreen
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var chatAPI: ChatAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DeepMedIQTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Routes.SPLASH_SCREEN
                ){
                    composable(Routes.SPLASH_SCREEN) {
                        SplashScreen {
                            navController.navigate(Routes.ENTRY_SCREEN)
                        }
                    }

                    composable(
                        Routes.ENTRY_SCREEN
                    ){
                        DeepMedIQApp(
                            onNavigate = {
                                navController.navigate(it.route)
                            }
                        )
                    }
                    composable(
                        route = Routes.LOGIN_SCREEN
                    ){
                        LoginScreen(
                            onNavigate = {
                                navController.navigate(it.route)
                            }
                        )
                    }
                    composable(
                        Routes.SIGNUP_SCREEN
                    ){
                        SignupScreen(
                            onNavigate = {
                                navController.navigate(it.route)
                            }
                        )
                    }
                }

            }
        }

    }


}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeepMedIQApp(
    onNavigate: (UiEvent.Navigate) -> Unit = {},
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    // user details
    val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val name = prefs.getString("name", "")
    val email = prefs.getString("email", "")
    val photoUrl = prefs.getString("photo","")



    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    LaunchedEffect(drawerState) {
        snapshotFlow { drawerState.isOpen }.collect { isOpen ->
            Log.d("DrawerState", "Drawer is open: $isOpen")
        }
    }
    var selectedScreen by remember { mutableStateOf("Chat") }

    val entryScreenViewModel: EntryScreenViewModel = hiltViewModel()
    val dbChats = entryScreenViewModel.chats.collectAsState( emptyList())


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color(0xFF706c6c), // Dark gray background
                modifier = Modifier
                    .width(280.dp)
                    .clip(RoundedCornerShape(topEnd = 16.dp, bottomEnd = 0.dp)) // Rounded top edge
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 0.dp, bottom = 10.dp)
                        .background(Color.White, shape = RoundedCornerShape(12.dp))
                        .padding(16.dp), // Inner padding for the logo
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_app_logo),
                        contentDescription = "App Logo",
                        modifier = Modifier.size(120.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black, shape = RoundedCornerShape(12.dp)) // Outer black background
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black, shape = RoundedCornerShape(12.dp)) // Inner white box
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "History (${dbChats.value.size} items)",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        LazyColumn {
                            items(dbChats.value) { item ->
                                ChatItemBox(
                                    id = item.id.toString(),
                                    input = item.input ?: "",
                                    output = item.output ?: "",
                                    onClick = {
                                        entryScreenViewModel.onItemClick(item.id)
                                        scope.launch { drawerState.close() }
                                    },
                                    timeStamp = item.timeStamp.toString()
                                )
                            }
                        }
                    }
                }

            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            UserProfileImage(context)

                            Spacer(modifier = Modifier.width(12.dp)) // Space between image and logo

                            Image(
                                painter = painterResource(id = R.drawable.ic_app_logo),
                                contentDescription = "App Logo",
                                modifier = Modifier
                                    .size(150.dp)
                                    .clickable {
                                        entryScreenViewModel.invertFirstLoad()
                                    },
                                contentScale = ContentScale.Fit
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        Spacer(modifier = Modifier.width(16.dp)) // Add padding between title and button

                        val buttonShape = RoundedCornerShape(8.dp)

                        Box(
                            modifier = Modifier
                                .clip(buttonShape)
                                .background(Color(0xFFF0F0F0)) // light gray
                                .clickable {
                                    if (name == "") {
                                        onNavigate(UiEvent.Navigate(Routes.LOGIN_SCREEN))
                                    } else {
                                        Firebase.auth.signOut()
                                        val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                                        prefs.edit().clear().apply()
                                        onNavigate(UiEvent.Navigate(Routes.LOGIN_SCREEN))
                                    }
                                }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = if (name == "") "Log in" else "Logout",
                                color = Color.Black,
                                fontSize = 16.sp
                            )
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


@Composable
fun UserProfileImage(context: Context) {
    val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val photoUrl = prefs.getString("photo", null)
    val userName = prefs.getString("name", null)
    val userEmail = prefs.getString("email", null)
    Log.d("userName", "User Name: $userName")
    Log.d("userEmail", "User Email: $userEmail")
    Log.d("UserProfileImage", "Photo URL: $photoUrl")

    if (photoUrl != null) {
        Image(
            painter = rememberAsyncImagePainter(photoUrl),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .border(2.dp, Color.Gray, CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}


@Composable
fun ChatItemBox(
    id: String,
    input: String,
    output: String,
    timeStamp: String,
    onClick:()-> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(2.dp, Color.Gray, RoundedCornerShape(12.dp))
            .padding(12.dp)
            .clickable{
                onClick()
            }
    ) {
        Column {
            Text(text = " $input", color = Color.White, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = timeStamp, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
        }
    }
}

// Helper function to copy assets
private fun copyAssetsToDirectory(assetManager: AssetManager, assetPath: String, targetDir: File) {
    targetDir.mkdirs()
    assetManager.list(assetPath)?.forEach { asset ->
        val srcPath = "$assetPath/$asset"
        val destFile = File(targetDir, asset)

        if (assetManager.list(srcPath)?.isNotEmpty() == true) {
            // It's a subdirectory - recurse
            copyAssetsToDirectory(assetManager, srcPath, destFile)
        } else {
            // It's a file - copy it
            assetManager.open(srcPath).use { inputStream ->
                FileOutputStream(destFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        }
    }
}



// this is the splash scree
@Composable
fun SplashScreen(
    onInitializationComplete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.ic_app_logo),
            contentDescription = "App Logo",
            modifier = Modifier.size(120.dp)
        )

        LaunchedEffect(Unit) {
                delay(1000) // Minimum display time
                onInitializationComplete()
        }
    }
}