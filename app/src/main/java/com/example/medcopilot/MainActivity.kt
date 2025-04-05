package com.example.medcopilot

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.medcopilot.EntryScreen.ChatScreen
import com.example.medcopilot.EntryScreen.EntryScreenViewModel
import com.example.medcopilot.api.ChatAPI
import com.example.medcopilot.util.GoogleSignInUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import org.vosk.Recognizer
import org.vosk.Model
import org.vosk.android.SpeechService
import org.vosk.android.RecognitionListener
import android.Manifest
import android.content.res.AssetManager
import android.util.Log
import java.io.File
import java.io.FileOutputStream


@AndroidEntryPoint
class MainActivity : ComponentActivity(), RecognitionListener {

    @Inject
    lateinit var chatAPI: ChatAPI
    lateinit var assetManager: AssetManager

    private var speechService: SpeechService? = null
    private lateinit var model: Model

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request microphone permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 1)
        }

        setContent {
            MedCopilotApp()
        }
    }

    private fun initVosk() {
        Thread {

            // Initialize the asset manager
            assetManager = assets

            try {
                // 1. First debug what's in your assets
                Log.d("AssetsDebug", "Root assets contents:")
                assetManager.list("")?.forEach { item ->
                    Log.d("AssetsDebug", "- $item")

                    assetManager.list(item)?.let { subItems ->
                        if (subItems.isNotEmpty()) {
                            Log.d("AssetsDebug", "  (Directory containing ${subItems.size} items)")
                        }
                    }
                }

                // 2. Define model paths
                val modelAssetPath = "models/vosk-model" // Path in assets
                val modelDestDir = File(filesDir, "vosk-model") // Destination in internal storage

                // 3. Verify model exists in assets
                val hasModel = assetManager.list(modelAssetPath)?.isNotEmpty() == true
                if (!hasModel) {
                    throw Exception("Model not found in assets at path: $modelAssetPath")
                }

                // 4. Copy model from assets to internal storage if needed
                if (!modelDestDir.exists()) {
                    Log.d("ModelCopy", "Copying model from assets to $modelDestDir")
                    copyAssetsToDirectory(assetManager, modelAssetPath, modelDestDir)
                }

                // 5. Initialize Vosk model with the copied path
                model = Model(modelDestDir.absolutePath)
                Log.d("ModelInit", "Successfully loaded model from ${modelDestDir.absolutePath}")

            } catch (e: Exception) {
                Log.e("ModelLoading", "Failed to load model", e)
                // Handle error appropriately
            }
            val recognizer = Recognizer(model, 16000.0f)
            speechService = SpeechService(recognizer, 16000.0f)
            speechService?.startListening(this)
        }.start()
    }

    override fun onResult(hypothesis: String?) {
        Log.d("VoskResult", hypothesis ?: "null")
    }

    override fun onFinalResult(hypothesis: String?) {
        Log.d("VoskFinal", hypothesis ?: "null")
    }

    override fun onPartialResult(hypothesis: String?) {}
    override fun onError(e: java.lang.Exception?) {
        Log.e("VoskError", e?.message ?: "error")
    }

    override fun onTimeout() {}
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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_app_logo),
                        contentDescription = "App Logo",
                        modifier = Modifier.size(150.dp),
                        contentScale = ContentScale.Fit // Ensures full image display without extra space
                    )
                }

                // Drawer items
                LazyColumn {
                    items(dbChats.value) { item ->
                        ChatItemBox(
                            id = item.id.toString(),
                            input = item.input ?: "",
                            output = item.output ?: "",
                            onClick = {
                                entryScreenViewModel.onItemClick(item.id)
                                scope.launch { drawerState.close() } // Close the drawer
                            },
                            timeStamp = item.timeStamp.toString()
                        )
                    }
                }

            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Image(
                            painter = painterResource(id = R.drawable.ic_app_logo),
                            contentDescription = "App Logo",
                            modifier = Modifier.size(150.dp),
                            contentScale = ContentScale.Fit // Ensures full image display without extra space
                        )

                    },
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
            Text(text = " $input", color = Color.Black, style = MaterialTheme.typography.bodyMedium)
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

@Composable
fun AppLogo(){

}