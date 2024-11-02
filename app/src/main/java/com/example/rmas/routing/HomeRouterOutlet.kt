package com.example.rmas.routing

import android.content.ContentResolver
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
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

@OptIn(ExperimentalMaterial3Api::class)
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
    filtersViewModel: FiltersViewModel,
    mapItemsViewModel: MapItemsViewModel = viewModel()
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
                tags = filtersViewModel.userTags,
                setTag = {id: String, value: Boolean -> filtersViewModel.setTagValue(id, value) },
                isAddMapItemScreenVisible = isAddMapItemScreenVisible,
                openAddMapItemScreen = openAddMapItemScreen,
                closeAddMapItemScreen = closeAddMapItemScreen,
                mapItems,
                selectedMapItem = selectedMapItem.value,
                selectMapItem = { item ->
                    mapItemsViewModel.selectItem(item)
                },
                deselectMapItem = {
                    mapItemsViewModel.deselectItem()
                }
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
            )
        }
        composable(HomeRoutes.USERS_SCREEN) {
            Users(
                innerPadding,
            )
        }
    }
}