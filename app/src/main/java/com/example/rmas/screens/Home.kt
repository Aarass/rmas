package com.example.rmas.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun Home(
    signOut: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Map()

        SmallFloatingActionButton(
            onClick = signOut,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.CenterEnd),
            containerColor = Color.White,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = "Clear",
                tint = MaterialTheme.colorScheme.secondary
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            // TODO
            // UI
        }
    }
}

@Composable
fun Map() {
    val nis = LatLng(43.321445, 21.896104)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(nis, 15f)
    }

    val uiSettings  = remember {
        MapUiSettings(
            zoomControlsEnabled = false
        )
    }
    val properties = remember {
        MapProperties(
            mapType = MapType.NORMAL,
            isMyLocationEnabled = true,
        )
    }


    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = properties,
        uiSettings = uiSettings,
        contentPadding = WindowInsets.navigationBars.add(WindowInsets.statusBars).asPaddingValues(),
    ) {
        Marker(
            state = MarkerState(position = nis),
            title = "Nis",
            snippet = "Marker in Nis"
        )
    }
}
