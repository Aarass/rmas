package com.example.rmas.screens.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.rmas.R
import com.example.rmas.models.User
import com.example.rmas.viewModels.LeaderboardViewModel

@Composable
fun Users(leaderboardViewModel: LeaderboardViewModel = viewModel()) {
    LaunchedEffect(Unit) {
        Log.i("uio", "sad")
        leaderboardViewModel.fetch()
    }
    val leaderboard = leaderboardViewModel.leaderboard

    val first = if (0 in leaderboard.indices) leaderboard[0] else null
    val second = if (1 in leaderboard.indices) leaderboard[1] else null
    val third = if (2 in leaderboard.indices) leaderboard[2] else null

    Column {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(0f, 0f, 100f, 100f))
                .background(MaterialTheme.colorScheme.primary)
                .padding(vertical = 16.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Leaderboard", color = MaterialTheme.colorScheme.onPrimary)
            Row(
                modifier = Modifier.padding(bottom = 30.dp, top = 20.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                Box(modifier = Modifier.weight(1f)) { second?.let { SpotlightCard(second, 2) } }
                Box(modifier = Modifier.weight(1f)) { first?.let { SpotlightCard(first, 1) } }
                Box(modifier = Modifier.weight(1f)) { third?.let { SpotlightCard(third, 3) } }
            }
        }
        LazyColumn {
            itemsIndexed(leaderboard.drop(3)) { i, user ->
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(modifier = Modifier.padding(end = 16.dp), text = "${i + 4}")

                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(user.imageUrl)
                                .placeholder(R.drawable.avatar)
                                .error(R.drawable.no_image)
                                .build(),
                            contentDescription = "",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .padding(6.dp)
                                .size(30.dp)
                                .clip(CircleShape)
                        )
                        Text(user.fullName)
                    }

                    Text(text = user.points.toString(), color = MaterialTheme.colorScheme.primary)
                }
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun SpotlightCard(user: User, rank: Int) {
    Box(
        contentAlignment = Alignment.TopCenter
    ) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = user.points.toString(), color = MaterialTheme.colorScheme.primary)
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(user.imageUrl)
                        .placeholder(R.drawable.avatar)
                        .error(R.drawable.no_image)
                        .build(),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(6.dp)
                        .size(80.dp)
                        .clip(CircleShape)
                )

                Box(
                    modifier = Modifier.height(60.dp).padding(horizontal = 6.dp)
                ) {
                    Text(user.fullName, textAlign = TextAlign.Center, fontSize = 14.sp, lineHeight = 14.sp)
                }
            }
        }
        Box(

            modifier = Modifier.offset(y = 20.dp).clip(CircleShape).size(40.dp).background(Color(0xFF254665)).align(Alignment.BottomCenter),
            contentAlignment = Alignment.Center
        ) {
            Text(rank.toString() , color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}