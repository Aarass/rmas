package com.example.rmas.screens.home

import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.rmas.R
import com.example.rmas.routing.HomeRouterOutlet
import com.example.rmas.routing.HomeRoutes
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.maps.android.compose.MapsComposeExperimentalApi


@MapsComposeExperimentalApi
@ExperimentalPermissionsApi
@Composable
fun Home(
    openProfile: () -> Unit,
    locationClient: FusedLocationProviderClient
) {
    val navController = rememberNavController()

    val currentRoute = rememberSaveable { mutableStateOf(HomeRoutes.MAP_SCREEN) }

    val navigationBarItemColors = NavigationBarItemDefaults.colors().copy(
        selectedIconColor = MaterialTheme.colorScheme.primary,
        selectedTextColor = MaterialTheme.colorScheme.primary,
        selectedIndicatorColor = MaterialTheme.colorScheme.primary.copy(
            alpha = .1f
        ),
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .height(115.dp),
                containerColor = Color(MaterialTheme.colorScheme.background.toArgb())
            ) {
                NavigationBarItem(
                    colors = navigationBarItemColors,
                    selected = currentRoute.value == HomeRoutes.TABLE_SCREEN,
                    onClick = {
                        navController.navigate(HomeRoutes.TABLE_SCREEN)
                        currentRoute.value = HomeRoutes.TABLE_SCREEN
                    },
                    icon = {
                        Icon(
//                            imageVector = Icons.Outlined.GridView,
                            imageVector = if (currentRoute.value == HomeRoutes.TABLE_SCREEN) ImageVector.vectorResource(R.drawable.tablefilled) else ImageVector.vectorResource(R.drawable.tableoutlined),
                            contentDescription = "Clear",
                        )
                    },
                    label = {
                        Text(text = "Table")
                    }
                )
                NavigationBarItem(
                    colors = navigationBarItemColors,
                    selected = currentRoute.value == HomeRoutes.MAP_SCREEN,
                    onClick = {
                        navController.navigate(HomeRoutes.MAP_SCREEN)
                        currentRoute.value = HomeRoutes.MAP_SCREEN
                    },
                    icon = {
                        Icon(
//                            imageVector = Icons.Outlined.Map,
                            imageVector = if (currentRoute.value == HomeRoutes.MAP_SCREEN) Icons.Filled.Map else Icons.Outlined.Map,
                            contentDescription = "Clear",
                        )
                    },
                    label = {
                        Text(text = "Map")
                    }
                )
                NavigationBarItem(
                    colors = navigationBarItemColors,
                    selected = currentRoute.value == HomeRoutes.USERS_SCREEN,
                    onClick = {
                        navController.navigate(HomeRoutes.USERS_SCREEN)
                        currentRoute.value = HomeRoutes.USERS_SCREEN
                    },
                    icon = {
                        Icon(
//                            imageVector = Icons.Outlined.Leaderboard,
                            imageVector = if (currentRoute.value == HomeRoutes.USERS_SCREEN) Icons.Filled.Leaderboard else Icons.Outlined.Leaderboard,
                            contentDescription = "Clear",
                        )
                    },
                    label = {
                        Text(text = "Leaderboard")
                    }
                )
            }
        }
    ) { innerPadding ->
        HomeRouterOutlet(innerPadding, locationClient, navController, openProfile)
    }
}