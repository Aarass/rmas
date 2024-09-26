package com.example.rmas.screens.home

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.PeopleOutline
import androidx.compose.material.icons.outlined.TableChart
import androidx.compose.material.icons.outlined.TableView
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.rmas.routing.HomeRouterOutlet
import com.example.rmas.routing.HomeRoutes


@Composable
fun Home(
    openProfile: () -> Unit
) {
    val navController = rememberNavController()

    Log.i("Mat", MaterialTheme.colorScheme.background.toString())

    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .height(115.dp),
                containerColor = Color(MaterialTheme.colorScheme.background.toArgb())
            ) {
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        navController.navigate(HomeRoutes.TABLE_SCREEN)
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.GridView,
                            contentDescription = "Clear",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    },
                    label = {
                        Text(text = "Table")
                    }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = {
                        navController.navigate(HomeRoutes.MAP_SCREEN)
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Map,
                            contentDescription = "Clear",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    },
                    label = {
                        Text(text = "Map")
                    }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        navController.navigate(HomeRoutes.USERS_SCREEN)
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Leaderboard,
                            contentDescription = "Clear",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    },
                    label = {
                        Text(text = "Leaderboard")
                    }
                )
            }
        }
    ) { innerPadding ->
        HomeRouterOutlet(innerPadding, navController, openProfile)
    }
}