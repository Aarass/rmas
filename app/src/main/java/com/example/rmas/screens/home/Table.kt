package com.example.rmas.screens.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.rmas.models.MapItem

@Composable
fun Table (
    innerPadding: PaddingValues,
    mapItems: List<MapItem>,
) {
    LazyColumn(modifier = Modifier.padding(innerPadding)) {
        items(
            mapItems,
            key = { item -> item.id }
        ) { item ->
            Text(text = item.id)
            HorizontalDivider()
        }
    }
}
