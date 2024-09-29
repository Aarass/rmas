package com.example.rmas.routing

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.rmas.screens.home.Map
import com.example.rmas.screens.home.Table
import com.example.rmas.screens.home.Users
import com.example.rmas.viewModels.FiltersViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.maps.android.compose.MapsComposeExperimentalApi

object HomeRoutes {
    const val MAP_SCREEN = "MapScreen"
    const val TABLE_SCREEN = "TableScreen"
    const val USERS_SCREEN = "UsersScreen"
}

@MapsComposeExperimentalApi
@ExperimentalPermissionsApi
@Composable
fun HomeRouterOutlet(innerPadding: PaddingValues, navController: NavHostController, openProfile: () -> Unit) {
    val filtersViewModel = viewModel<FiltersViewModel>()

    NavHost(navController, startDestination = HomeRoutes.MAP_SCREEN) {
        composable(HomeRoutes.MAP_SCREEN) {
            Map(
                innerPadding,
                openProfile,
                tags = filtersViewModel.userTags,
                setTag = {id: String, value: Boolean -> filtersViewModel.setTagValue(id, value) },
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