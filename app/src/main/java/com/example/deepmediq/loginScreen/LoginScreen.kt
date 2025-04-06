package com.example.deepmediq.loginScreen

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedButton
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.deepmediq.R
import com.example.deepmediq.util.GoogleSignInUtils
import com.example.deepmediq.util.Routes
import com.example.deepmediq.util.UiEvent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import kotlinx.coroutines.CoroutineScope
import android.util.Log
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.google.firebase.auth.OAuthCredential
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

@Composable
fun LoginScreen(
    onNavigate: (UiEvent.Navigate) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        GoogleSignInUtils.doGoogleSignIn(
            context = context,
            scope = scope,
            launcher = null,
            login = {
                Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
            }
        )
    }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()), // ✅ Enables scroll
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { onNavigate(UiEvent.Navigate(Routes.ENTRY_SCREEN)) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007F5F))
        ) {
            Text("Home")
        }

            EmailLoginScreen(
                email,
                password,
                onEmailChange = {email = it},
                onPasswordChange = {password = it},
                onLoginClick = {
                    loginWithEmail(context, email, password, onNavigate)
                },
                onSignupClick = {
                  onNavigate(UiEvent.Navigate(Routes.SIGNUP_SCREEN))
                }
                )


        AuthButton("Sign in with Google", painterResource(id = R.drawable.img), context, scope, launcher, onNavigate)
        MicrosoftAuthButton("Sign in with Microsoft", painterResource(id = R.drawable.ic_microsoft), context, onNavigate)

    }
}

@Composable
fun EmailLoginScreen(
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onSignupClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Login", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            visualTransformation = PasswordVisualTransformation()
        )

        Button(
            onClick = onLoginClick,
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
        ) {
            Text("Login")
        }
        TextButton(onClick = onSignupClick) {
            Text(
                "Don't have an account? Sign up",
                color = Color.Black
            )
        }

    }
}


@Composable
fun AuthButton(text: String, icon: Painter, context: Context, scope: CoroutineScope, launcher: ManagedActivityResultLauncher<Intent, ActivityResult>, onNavigate: (UiEvent.Navigate) -> Unit) {
    OutlinedButton(
        onClick = {
            GoogleSignInUtils.doGoogleSignIn(
                context = context,
                scope = scope,
                launcher = launcher,
                login = {
                    val user = FirebaseAuth.getInstance().currentUser
                    user?.let {
                        val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                        prefs.edit()
                            .putString("uid", it.uid)
                            .putString("name", it.displayName)
                            .putString("email", it.email)
                            .putString("photo", it.photoUrl?.toString())
                            .apply()
                        Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                        onNavigate(UiEvent.Navigate(Routes.ENTRY_SCREEN))
                    }
                }
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        border = BorderStroke(1.dp, Color.Gray)
    ) {
        Icon(painter = icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.Unspecified)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text)
    }
}

@Composable
fun MicrosoftAuthButton(
    text: String,
    icon: Painter,
    context: Context,
    onNavigate: (UiEvent.Navigate) -> Unit
) {
    OutlinedButton(
        onClick = {
            val provider = OAuthProvider.newBuilder("microsoft.com")
            val pendingResultTask = FirebaseAuth.getInstance().pendingAuthResult

            if (pendingResultTask != null) {
                pendingResultTask
                    .addOnSuccessListener {
                        saveUserToPrefs(context, onNavigate)
                    }
                    .addOnFailureListener { e ->
                        Log.e("MicrosoftLogin", "Pending result failed: ${e.message}", e)
                        Toast.makeText(context, "Microsoft Sign-In Failed", Toast.LENGTH_SHORT).show()
                    }
            } else {
                FirebaseAuth.getInstance()
                    .startActivityForSignInWithProvider(context as Activity, provider.build())
                    .addOnSuccessListener {
                        saveUserToPrefs(context, onNavigate)
                    }
                    .addOnFailureListener { e ->
                        Log.e("MicrosoftLogin", "Sign-In Error: ${e.message}", e)
                        Toast.makeText(context, "Microsoft Sign-In Error", Toast.LENGTH_SHORT).show()
                    }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        border = BorderStroke(1.dp, Color.Gray)
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = Color.Unspecified
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text)
    }
}


private fun saveUserToPrefs(context: Context, onNavigate: (UiEvent.Navigate) -> Unit) {
    val user = FirebaseAuth.getInstance().currentUser
    user?.getIdToken(false)?.addOnSuccessListener { result ->
        val accessToken = result.token ?: return@addOnSuccessListener

        val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val name = user.displayName
        val email = user.email
        val uid = user.uid

        // Use coroutine to fetch photo from Microsoft Graph
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("https://graph.microsoft.com/v1.0/me/photo/\$value")
                val connection = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    setRequestProperty("Authorization", "Bearer $accessToken")
                }

                val imageBytes = connection.inputStream.readBytes()
                val base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT)

                prefs.edit()
                    .putString("uid", uid)
                    .putString("name", name)
                    .putString("email", email)
                    .putString("photo", base64Image) // Saved as Base64
                    .apply()

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Microsoft login successful", Toast.LENGTH_SHORT).show()
                    onNavigate(UiEvent.Navigate(Routes.ENTRY_SCREEN))
                }
            } catch (e: Exception) {
                Log.e("MicrosoftPhoto", "Failed to fetch profile photo", e)
                prefs.edit()
                    .putString("uid", uid)
                    .putString("name", name)
                    .putString("email", email)
                    .putString("photo", null)
                    .apply()

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Login success, but no photo", Toast.LENGTH_SHORT).show()
                    onNavigate(UiEvent.Navigate(Routes.ENTRY_SCREEN))
                }
            }
        }
    }
}

fun registerWithEmail(context: Context, email: String, password: String, onNavigate: (UiEvent.Navigate) -> Unit) {
    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
        .addOnSuccessListener {
            Toast.makeText(context, "Registration successful", Toast.LENGTH_SHORT).show()
            onNavigate(UiEvent.Navigate(Routes.ENTRY_SCREEN))
        }
        .addOnFailureListener {
            Toast.makeText(context, "Registration failed: ${it.message}", Toast.LENGTH_SHORT).show()
        }
}

fun loginWithEmail(
    context: Context,
    email: String,
    password: String,
    onNavigate: (UiEvent.Navigate) -> Unit
) {
    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
        .addOnSuccessListener {
            val user = FirebaseAuth.getInstance().currentUser
            user?.reload()?.addOnCompleteListener { reloadTask ->
                if (user.isEmailVerified) {
                    // Store user data in SharedPreferences
                    val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                    prefs.edit()
                        .putString("uid", user.uid)
                        .putString("name", user.displayName ?: "")
                        .putString("email", user.email ?: "")
                        .putString("photo", null) // Set photo if needed
                        .apply()

                    Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                    onNavigate(UiEvent.Navigate(Routes.ENTRY_SCREEN))
                } else {
                    Toast.makeText(context, "Please verify your email to login", Toast.LENGTH_LONG).show()
                    FirebaseAuth.getInstance().signOut()
                }
            }
        }
        .addOnFailureListener {
            Toast.makeText(context, "Login failed: ${it.message}", Toast.LENGTH_SHORT).show()
        }
}

