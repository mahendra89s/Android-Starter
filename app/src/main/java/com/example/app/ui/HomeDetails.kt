package com.example.app.ui

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.app.model.internal.uistates.HomeScreenUIState
import com.example.app.viewmodel.HomeViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun HomeDetails(
    navController: NavController,
    homeViewModel: HomeViewModel,
    articleName: String
) {
    val state by homeViewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).fillMaxSize()
        ) {
            Text(text = articleName)
            Button(
                onClick = {
                    navController.navigateUp()
                }
            ) {
                Text(text = "Go Back")
            }

            MediaPickerScreen()

            HomeContent(
                modifier = Modifier.padding(paddingValues),
                state = state,
                onRetry = {
                    homeViewModel.getHomeData()
                }
            )
        }
    }
}


@Composable
fun HomeContent(
    modifier: Modifier,
    state: HomeScreenUIState,
    onRetry: () -> Unit,
    onQuery: (String) -> Unit = {}
) {
    var textfield by remember {
        mutableStateOf(
            TextFieldValue("")
        )
    }
    var job: Job? = null
    val scope = rememberCoroutineScope()
    BasicTextField(
        value = textfield,
        onValueChange = {
            textfield = it
            job?.cancel()
            job = scope.launch {
                delay(500)
                onQuery(it.text)
            }
        }
    )

    if(state.isLoading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    if(state.errorMessage.isNotBlank()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = state.errorMessage)
            Button(
                onClick = {
                    onRetry()
                }
            ) {
                Text(text = "Retry")
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(state.articles.size) { index ->
            Text(
                text = state.articles[index].title ?: "No title",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}


@Composable
fun MediaPickerScreen() {
    val context = LocalContext.current

    // States to store selected file/image URIs
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    // 1. File / Image Picker Launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedFileUri = uri
        if (uri != null) {
            Toast.makeText(context, "File Selected: $uri", Toast.LENGTH_SHORT).show()
        }
    }

    // 2. Camera Capture Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            capturedImageUri = tempCameraUri
            Toast.makeText(context, "Image Captured successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    // 3. Runtime Camera Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            tempCameraUri = createImageUri(context)
            tempCameraUri?.let { cameraLauncher.launch(it) }
        } else {
            Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display URI States
        if (capturedImageUri != null) {
            Text(text = "Captured Image: ${capturedImageUri?.path.toString()}", modifier = Modifier.padding(8.dp))
        }
        if (selectedFileUri != null) {
            Text(text = "Picked File: ${selectedFileUri?.path.toString()}", modifier = Modifier.padding(8.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Trigger Camera Button
        Button(
            onClick = {
                // Requests permission directly; launches camera inside callback if already granted
                permissionLauncher.launch(android.Manifest.permission.CAMERA)
            },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Text("Open Camera")
        }

        // Trigger File Picker Button
        Button(
            onClick = {
                // Accepts MIME types (e.g., "image/*" for photos only, or "*/*" for any file)
                filePickerLauncher.launch("image/*")
            },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Text("Pick Image from Gallery")
        }
    }
}

/**
 * Helper function to generate a secure temporal File URI required by the Camera Contract.
 */
private fun createImageUri(context: Context): Uri {
    val directory = File(context.cacheDir, "images")
    if (!directory.exists()) directory.mkdirs()

    val file = File.createTempFile(
        "captured_photo_${System.currentTimeMillis()}",
        ".jpg",
        directory
    )

    val authority = "${context.packageName}.fileprovider"
    return FileProvider.getUriForFile(context, authority, file)
}

private fun getBytesFromUri(context: Context, uri: Uri): ByteArray? {
    return runCatching {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            inputStream.readBytes() // Kotlin extension to read entire stream into ByteArray
        }
    }.getOrNull() // Returns null safely if a file error occurs
}