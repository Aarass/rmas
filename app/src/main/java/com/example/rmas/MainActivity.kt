package com.example.rmas

import android.content.ContentValues
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore.Images
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts.TakePicture
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.rmas.routing.MainRouterOutlet
import com.example.rmas.ui.theme.RMASTheme
import com.example.rmas.viewModels.AuthViewModel
import com.example.rmas.viewModels.MainViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val mainViewModel by viewModels<MainViewModel>()
    private val authViewModel: AuthViewModel by viewModels<AuthViewModel>()

    private val launcher = registerForActivityResult(TakePicture()) { isSuccess ->
        if (isSuccess) {
            val selectedImageUriCopy = selectedImageUri
                ?: throw Exception("Image uri is null after successful intent")

            mainViewModel.setSelectedImageUri(selectedImageUriCopy)
        } else {
            // TODO
        }
    }

    private fun toast(string: String) {
        Toast.makeText(this, string, Toast.LENGTH_LONG).show()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        lifecycleScope.launch {
            mainViewModel.takeImageEvent.collect {
                dispatchTakePictureIntent()
            }
        }

        lifecycleScope.launch {
            authViewModel.errors.collect {
                toast(it)
            }
        }


        super.onCreate(savedInstanceState)
        enableEdgeToEdge(navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT))
        if (Build.VERSION.SDK_INT >= 29) {
            window.isNavigationBarContrastEnforced = false
        }
        setContent {
            RMASTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainRouterOutlet(contentResolver = contentResolver)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        authViewModel.tryToRestoreSession()
    }


    private var selectedImageUri: Uri? = null
    private fun dispatchTakePictureIntent() {
        val contentValues = ContentValues().apply {
            put(Images.Media.DISPLAY_NAME, "JPEG_${System.currentTimeMillis()}.jpg")
            put(Images.Media.MIME_TYPE, "image/jpeg")
            put(Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }

        val imageUri: Uri = contentResolver.insert(Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            ?: throw Exception("Couldn't reserve space for the future image")
        selectedImageUri = imageUri

        launcher.launch(imageUri)
    }
}