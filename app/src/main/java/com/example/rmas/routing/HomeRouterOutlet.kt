package com.example.rmas.routing

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.rmas.models.User
import com.example.rmas.screens.home.Map
import com.example.rmas.screens.home.Table
import com.example.rmas.screens.home.Users
import com.example.rmas.ui.theme.resetSystemNavigationTheme
import com.example.rmas.viewModels.FiltersViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.maps.android.compose.MapsComposeExperimentalApi
import kotlinx.coroutines.flow.Flow

object HomeRoutes {
    const val MAP_SCREEN = "MapScreen"
    const val TABLE_SCREEN = "TableScreen"
    const val USERS_SCREEN = "UsersScreen"
}

@MapsComposeExperimentalApi
@ExperimentalPermissionsApi
@Composable
fun HomeRouterOutlet(
    innerPadding: PaddingValues,
    locationClient: FusedLocationProviderClient,
    navController: NavHostController,
    currentUserFlow: Flow<User?>,
    signOut: () -> Unit,
    isAddMapItemScreenVisible: Boolean,
    openAddMapItemScreen: () -> Unit,
    closeAddMapItemScreen: () -> Unit,
    filtersViewModel: FiltersViewModel
) {
    NavHost(navController, startDestination = HomeRoutes.MAP_SCREEN) {
        composable(HomeRoutes.MAP_SCREEN) {
            Map(
                innerPadding,
                locationClient,
                currentUserFlow,
                signOut,
                tags = filtersViewModel.userTags,
                setTag = {id: String, value: Boolean -> filtersViewModel.setTagValue(id, value) },
                isAddMapItemScreenVisible,
                openAddMapItemScreen,
                closeAddMapItemScreen,
            )
        }
        composable(HomeRoutes.TABLE_SCREEN) {
            Table()
        }
        composable(HomeRoutes.USERS_SCREEN) {
            Users()
        }
    }
}