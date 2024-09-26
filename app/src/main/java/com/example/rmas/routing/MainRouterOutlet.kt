package com.example.rmas.routing

import android.content.ContentResolver
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rmas.enums.AuthStatus
import com.example.rmas.screens.home.Home
import com.example.rmas.screens.Profile
import com.example.rmas.screens.SignIn
import com.example.rmas.screens.SignUp
import com.example.rmas.viewModels.AuthViewModel
import com.example.rmas.viewModels.MainViewModel

private object MainRoutes {
    const val SIGN_IN_SCREEN = "SignInScreen"
    const val SIGN_UP_SCREEN = "SignUpScreen"
    const val HOME_SCREEN = "HomeScreen"
    const val PROFILE_SCREEN = "ProfileScreen"
}

@Composable
fun MainRouterOutlet(contentResolver: ContentResolver) {
    val navController = rememberNavController()

    val mainViewModel = viewModel<MainViewModel>()
    val authViewModel = viewModel<AuthViewModel>()

    LaunchedEffect(Unit) {
        authViewModel.onAuthStatusChange.collect() {
            Log.i("router", it.toString())
            if (it == AuthStatus.LogedIn) {
                navController.navigate(MainRoutes.HOME_SCREEN) {
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    }
                }
            } else {
                navController.navigate(MainRoutes.SIGN_IN_SCREEN) {
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    }
                }
            }
        }
    }

    NavHost(navController, startDestination = MainRoutes.SIGN_IN_SCREEN) {
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
                clearSelectedImage = { mainViewModel.clearSelectedImageUri() },
                imageUriFlow = mainViewModel.selectedImageUri,
                signUp = { name, surname, phoneNumber, email, password, imageUri ->
                    authViewModel.signUp(name, surname, phoneNumber, email, password, imageUri, contentResolver)
                },
                isSigningUpFlow = authViewModel.isSigningUp,
            )
        }
        composable(MainRoutes.HOME_SCREEN) {
            Home(
                openProfile = { navController.navigate(MainRoutes.PROFILE_SCREEN)}
            )
        }
        composable(MainRoutes.PROFILE_SCREEN) {
            Profile(signOut ={ authViewModel.signOut() })
        }
    }
}