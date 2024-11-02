package com.example.rmas.screens.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.rmas.models.MapItem

@Composable
fun Table (
    innerPadding: PaddingValues,
    mapItems: List<MapItem>,
) {
    LazyColumn(modifier = Modifier.padding(innerPadding)) {
        items(mapItems, key = { item -> item.id }) { item ->
            Text(text = item.id)
            Text(text = item.title)
            Text(text = "${item.location}")
            HorizontalDivider()
        }
    }
}
