package com.example.rmas.utility

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest


@Composable
fun ClearableImage(imageUri: Uri, clear: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(imageUri).build(),
            contentDescription = "User image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        SmallFloatingActionButton(
            onClick = clear,
            shape = CircleShape,
            modifier = Modifier.align(Alignment.TopEnd),
            containerColor = Color.White,
        ) {
            Icon(
                imageVector = Icons.Filled.Clear,
                contentDescription = "Clear",
                tint = MaterialTheme.colorScheme.secondary
            )
        }
    }
}