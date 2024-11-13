package com.example.rmas

import android.Manifest
import android.app.NotificationManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore.Images
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.TakePicture
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.rmas.routing.MainRouterOutlet
import com.example.rmas.ui.theme.RMASTheme
import com.example.rmas.viewModels.AuthViewModel
import com.example.rmas.viewModels.MainViewModel
import com.example.rmas.viewModels.MapItemsViewModel
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val mainViewModel by viewModels<MainViewModel>()
    private val authViewModel by viewModels<AuthViewModel>()
    private val mapItemsViewModel by viewModels<MapItemsViewModel>()

    private val launcher = registerForActivityResult(TakePicture()) { isSuccess ->
        if (isSuccess) {
            selectedImageUri?.let {
                mainViewModel.setSelectedImageUri(it)
            } ?: throw Exception("Image uri is null after successful intent")
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

        registerForActivityResult( ActivityResultContracts.RequestMultiplePermissions()) { grants ->
            if (grants.isEmpty()) { throw Exception("Probably, another request is active") }

            val foregroundService = grants[Manifest.permission.FOREGROUND_SERVICE]!!
            val location = grants[Manifest.permission.ACCESS_FINE_LOCATION]!!

            if (foregroundService && location) {
                val serviceIntent = Intent(this, LocationService::class.java)
                serviceIntent.action = LocationService.START
                startForegroundService(serviceIntent)
            }

            if (location) {
                val locationClient = LocationServices.getFusedLocationProviderClient(this)
                setContent {
                    RMASTheme {
                        MainRouterOutlet(contentResolver = contentResolver, locationClient)
                    }
                }
            }
        }.run { launch(arrayOf(Manifest.permission.FOREGROUND_SERVICE, Manifest.permission.ACCESS_FINE_LOCATION)) }
    }

    override fun onStart() {
        super.onStart()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val mapItemId = intent.getStringExtra("mapItemId")

        mapItemId?.let {
            notificationManager.cancelAll()

            mapItemsViewModel.setMapItemIdFromNotification(it)

            Log.i("bnm", "Received intent with map item id: $it")
        } ?: run {
            Log.i("bnm", "Received intent with no map item id")
        }

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