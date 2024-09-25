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
import com.example.rmas.screens.Home
import com.example.rmas.screens.SignIn
import com.example.rmas.screens.SignUp
import com.example.rmas.viewModels.AuthViewModel
import com.example.rmas.viewModels.MainViewModel

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun RouterOutlet(contentResolver: ContentResolver) {
    val navController = rememberNavController()

    val mainViewModel = viewModel<MainViewModel>()
    val authViewModel = viewModel<AuthViewModel>()

    LaunchedEffect(Unit) {
        authViewModel.onAuthStatusChange.collect() {
            Log.i("router", it.toString())
            if (it == AuthStatus.LogedIn) {
                navController.navigate(Routes.homeScreen)
            } else {
                navController.navigate(Routes.signInScreen)
            }
        }
    }

    NavHost(navController, startDestination = Routes.signInScreen) {
        composable(Routes.signInScreen) {
            SignIn(
                navigateToSignUpScreen = { navController.navigate(Routes.signUpScreen) },
                signIn = {email, password ->
                    authViewModel.signIn(email, password)
                },
                isSigningInFlow = authViewModel.isSigningIn,
            )
        }
        composable(Routes.signUpScreen) {
            SignUp(
                navigateToSignInScreen = { navController.navigate(Routes.signInScreen) },
                openCamera = { mainViewModel.takePicture() },
                clearSelectedImage = { mainViewModel.clearSelectedImageUri() },
                imageUriFlow = mainViewModel.selectedImageUri,
                signUp = { name, surname, phoneNumber, email, password, imageUri ->
                    authViewModel.signUp(name, surname, phoneNumber, email, password, imageUri, contentResolver)
                },
                isSigningUpFlow = authViewModel.isSigningUp,
            )
        }
        composable(Routes.homeScreen) {
            Home(signOut ={ authViewModel.signOut() })
        }
    }
}