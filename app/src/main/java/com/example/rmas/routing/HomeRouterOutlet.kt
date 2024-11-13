package com.example.rmas.routing

import android.content.ContentResolver
import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.rmas.models.User
import com.example.rmas.screens.home.Map
import com.example.rmas.screens.home.Table
import com.example.rmas.screens.home.Users
import com.example.rmas.viewModels.FiltersViewModel
import com.example.rmas.viewModels.MapItemsViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MapsComposeExperimentalApi

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
    navigateToMap: () -> Unit,
    currentUser: User?,
    signOut: () -> Unit,
    isAddMapItemScreenVisible: Boolean,
    openAddMapItemScreen: (location: LatLng) -> Unit,
    closeAddMapItemScreen: () -> Unit,
    contentResolver: ContentResolver,
    mapItemsViewModel: MapItemsViewModel = viewModel(),
    filtersViewModel: FiltersViewModel = viewModel(),
) {
    val mapItems = mapItemsViewModel.getMapItems()
    val selectedMapItem = mapItemsViewModel.getSelectedItem()

    NavHost(navController, startDestination = HomeRoutes.MAP_SCREEN) {
        composable(HomeRoutes.MAP_SCREEN) {
            Map(
                innerPadding = innerPadding,
                contentResolver = contentResolver,
                locationClient = locationClient,
                currentUser = currentUser,
                signOut = signOut,
                isAddMapItemScreenVisible = isAddMapItemScreenVisible,
                openAddMapItemScreen = openAddMapItemScreen,
                closeAddMapItemScreen = closeAddMapItemScreen,
                mapItems = mapItems,
                queryMapItems = { filters, location ->
                    mapItemsViewModel.queryMapItems(filters, location)
                },
                selectedMapItem = selectedMapItem.value,
                selectMapItem = { item ->
                    mapItemsViewModel.selectItem(item)
                },
                deselectMapItem = {
                    mapItemsViewModel.deselectItem()
                },
                filtersViewModel = filtersViewModel,
            )
        }
        composable(HomeRoutes.TABLE_SCREEN) {
            Table(
                innerPadding = innerPadding,
                mapItems,
                selectedMapItem = selectedMapItem.value,
                selectMapItem = { item ->
                    mapItemsViewModel.selectItem(item)
                },
                navigateToMap = navigateToMap,
                deselectMapItem = {
                    mapItemsViewModel.deselectItem()
                }
            )
        }
        composable(HomeRoutes.USERS_SCREEN) {
            Users(
            )
        }
    }
}