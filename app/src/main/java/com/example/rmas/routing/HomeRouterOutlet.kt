package com.example.rmas.routing

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.rmas.screens.home.Map
import com.example.rmas.screens.home.Table
import com.example.rmas.screens.home.Users

object HomeRoutes {
    const val MAP_SCREEN = "MapScreen"
    const val TABLE_SCREEN = "TableScreen"
    const val USERS_SCREEN = "UsersScreen"
}

@Composable
fun HomeRouterOutlet(innerPadding: PaddingValues, navController: NavHostController, openProfile: () -> Unit) {
    NavHost(navController, startDestination = HomeRoutes.USERS_SCREEN) {
        composable(HomeRoutes.MAP_SCREEN) {
            Map(innerPadding, openProfile)
        }
        composable(HomeRoutes.TABLE_SCREEN) {
            Table()
        }
        composable(HomeRoutes.USERS_SCREEN) {
            Users()
        }
    }
}