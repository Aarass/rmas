package com.example.rmas.screens.home

import android.app.Activity
import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.rmas.MainActivity
import com.example.rmas.R
import com.example.rmas.models.User
import com.example.rmas.routing.HomeRouterOutlet
import com.example.rmas.routing.HomeRoutes
import com.example.rmas.ui.theme.resetSystemNavigationTheme
import com.example.rmas.ui.theme.setDarkStatusBarIcons
import com.example.rmas.viewModels.AuthViewModel
import com.example.rmas.viewModels.MapItemsViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MapsComposeExperimentalApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow


@MapsComposeExperimentalApi
@ExperimentalPermissionsApi
@Composable
fun Home(
    currentUserFlow: Flow<User?>,
    signOut: () -> Unit,
    openCamera: () -> Unit,
    newImageUriFlow: SharedFlow<Uri>,
    locationClient: FusedLocationProviderClient,
    contentResolver: ContentResolver,
) {
    // Nav host iz nekog razloga ima svoj viewModel provider, ali meni treba ista instanca viewModel-a kao u mainActivity-u
    val mapItemsViewModel = viewModel<MapItemsViewModel >(LocalContext.current as MainActivity)

    val window = (LocalContext.current as Activity).window

    val currentUser by currentUserFlow.collectAsState(null)

    val navController = rememberNavController()
    val currentRoute = rememberSaveable { mutableStateOf(HomeRoutes.MAP_SCREEN) }

    val navigationBarItemColors = NavigationBarItemDefaults.colors().copy(
        selectedIconColor = MaterialTheme.colorScheme.primary,
        selectedTextColor = MaterialTheme.colorScheme.primary,
        selectedIndicatorColor = MaterialTheme.colorScheme.primary.copy(
            alpha = .1f
        ),
    )

    var addMapItemSavedVisibilityState by rememberSaveable() { mutableStateOf(false) }
    val addMapItemVisibilityState = MutableTransitionState(addMapItemSavedVisibilityState)
    var addMapItemAtLocation: LatLng? by remember { mutableStateOf(null) }

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
        HomeRouterOutlet(
            innerPadding = innerPadding,
            locationClient = locationClient,
            navController = navController,
            currentUser = currentUser,
            signOut = signOut,
            isAddMapItemScreenVisible = addMapItemVisibilityState.currentState,
            openAddMapItemScreen = { location ->
                addMapItemAtLocation = location
                Log.i("asd", "postavljena lokacija ${location}")

                addMapItemVisibilityState.targetState = true
                addMapItemSavedVisibilityState = true
                resetSystemNavigationTheme(window)
            },
            closeAddMapItemScreen = {
                addMapItemVisibilityState.targetState = false
                addMapItemSavedVisibilityState = false
                setDarkStatusBarIcons(window)
            },
            contentResolver = contentResolver,
            navigateToMap = {
                navController.navigate(HomeRoutes.MAP_SCREEN)
                currentRoute.value = HomeRoutes.MAP_SCREEN
            },
            mapItemsViewModel = mapItemsViewModel,
        )
    }

    val coroutineScope = rememberCoroutineScope()


    var isLoadingIndicatorVisible by remember { mutableStateOf(false) }

    AddMapItemScreen(
        visibility = addMapItemVisibilityState,
        close = {
            addMapItemVisibilityState.targetState = false
            addMapItemSavedVisibilityState = false
            setDarkStatusBarIcons(window)
        },
        newImageUriFlow = newImageUriFlow,
        openCamera = openCamera,
        author = currentUser,
        location = addMapItemAtLocation,
        longLastingCoroutineScope = coroutineScope,
        contentResolver = contentResolver,
        startLoadingAnimation = {
            isLoadingIndicatorVisible = true
        },
        stopLoadingAnimation = {
            isLoadingIndicatorVisible = false
        },
        onNewMapItem = { item ->
            mapItemsViewModel.addNewMapItem(item)

        }
    )

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoadingIndicatorVisible) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth().height(5.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
    }
}