package com.example.rmas.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FilterAlt
import androidx.compose.material.icons.rounded.RemoveRedEye
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.rmas.R
import com.example.rmas.models.MapItem

@Composable
fun Table (
    innerPadding: PaddingValues,
    mapItems: List<MapItem>,
    navigateToMap: () -> Unit,
    selectedMapItem: MapItem?,
    selectMapItem: (MapItem) -> Unit,
) {
    val roundness = RoundedCornerShape(20.dp)
    val height = 100.dp;

    LazyColumn(modifier = Modifier.padding(innerPadding)) {
        items(mapItems, key = { item -> item.id }) { item ->
            val color =
                if (item.id == selectedMapItem?.id)
                    MaterialTheme.colorScheme.primary.copy(.5f)
                else
                    MaterialTheme.colorScheme.primary.copy(.1f)
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .fillMaxWidth()
                    .height(height)
                    .clip(roundness)
                    .background(color),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val url = if (item.images.isNotEmpty()) item.images.first() else ""
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(url)
                            .placeholder(R.drawable.avatar)
                            .error(R.drawable.no_image)
                            .build(),
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(height)
                            .clip(roundness)
                    )
                    Column(
                        modifier = Modifier.padding(start = 20.dp)
                    ) {
                        Text(text = item.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        if (item.description.length > 20) {
                            Text(text = "${item.description.substring(0, 20)}...")
                        } else {
                            Text(text = item.description)
                        }
                    }
                }

                IconButton(
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .size(50.dp),
                    onClick = {
                        selectMapItem(item)
                        navigateToMap()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.RemoveRedEye,
                        contentDescription = "Filters icon",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}
