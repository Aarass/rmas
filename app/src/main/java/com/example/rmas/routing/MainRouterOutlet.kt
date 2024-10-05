package com.example.rmas.routing

import android.content.ContentResolver
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rmas.enums.AuthStatus
import com.example.rmas.screens.SignIn
import com.example.rmas.screens.SignUp
import com.example.rmas.screens.home.Home
import com.example.rmas.viewModels.AuthViewModel
import com.example.rmas.viewModels.MainViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.maps.android.compose.MapsComposeExperimentalApi

private object MainRoutes {
    const val SIGN_IN_SCREEN = "SignInScreen"
    const val SIGN_UP_SCREEN = "SignUpScreen"
    const val HOME_SCREEN = "HomeScreen"
}

@OptIn(ExperimentalPermissionsApi::class, MapsComposeExperimentalApi::class)
@Composable
fun MainRouterOutlet(contentResolver: ContentResolver, locationClient: FusedLocationProviderClient) {
    val navController = rememberNavController()

    val mainViewModel = viewModel<MainViewModel>()
    val authViewModel = viewModel<AuthViewModel>()

    LaunchedEffect(Unit) {
        authViewModel.onAuthStatusChange.collect {
            if (it == AuthStatus.LogedIn) {
                if (navController.currentDestination?.route == MainRoutes.HOME_SCREEN) return@collect
                navController.navigate(MainRoutes.HOME_SCREEN) {
                    popUpTo(navController.graph.id) {
                        inclusive = false
                    }
                }
            } else {
                if (navController.currentDestination?.route == MainRoutes.SIGN_IN_SCREEN) return@collect
                navController.navigate(MainRoutes.SIGN_IN_SCREEN) {
                    popUpTo(navController.graph.id) {
                        inclusive = false
                    }
                }
            }
        }
    }

    NavHost(
        navController,
        startDestination = MainRoutes.SIGN_IN_SCREEN,
        modifier = Modifier
            .fillMaxSize(),
        ) {
        composable(MainRoutes.SIGN_IN_SCREEN) {
            SignIn(
                navigateToSignUpScreen = { navController.navigate(MainRoutes.SIGN_UP_SCREEN) },
                signIn = {email, password ->
                    authViewModel.signIn(email, password)
                },
                isSigningInFlow = authViewModel.isSigningIn,
            )
        }
        composable(MainRoutes.SIGN_UP_SCREEN) {
            SignUp(
                navigateToSignInScreen = { navController.navigate(MainRoutes.SIGN_IN_SCREEN) },
                openCamera = { mainViewModel.takePicture() },
                newImageUriFlow = mainViewModel.newImageUri,
                signUp = { name, surname, phoneNumber, email, password, imageUri ->
                    authViewModel.signUp(name, surname, phoneNumber, email, password, imageUri, contentResolver)
                },
                isSigningUpFlow = authViewModel.isSigningUp,
            )
        }
        composable(MainRoutes.HOME_SCREEN) {
            Home(
                signOut ={ authViewModel.signOut() },
                currentUserFlow = authViewModel.currentUser,
                newImageUriFlow = mainViewModel.newImageUri,
                openCamera = { mainViewModel.takePicture() },
                locationClient = locationClient,
            )
        }
    }
}