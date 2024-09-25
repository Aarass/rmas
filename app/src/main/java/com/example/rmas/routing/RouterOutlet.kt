package com.example.rmas.routing

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rmas.screens.SignIn
import com.example.rmas.screens.SignUp
import com.example.rmas.viewModels.AuthViewModel
import com.example.rmas.viewModels.MainViewModel

@Composable
fun RouterOutlet() {
    val navController = rememberNavController()

    val mainViewModel = viewModel<MainViewModel>()
    val authViewModel = viewModel<AuthViewModel>()

    NavHost(navController, startDestination = Routes.signInScreen) {
        composable(Routes.signInScreen) {
            SignIn(navigateToSignUpScreen = { navController.navigate(Routes.signUpScreen) })
        }
        composable(Routes.signUpScreen) {
            SignUp(
                navigateToSignInScreen = { navController.navigate(Routes.signInScreen) },
                openCamera = { mainViewModel.takePicture() },
                clearSelectedImage = { mainViewModel.clearSelectedImageUri() },
                imageUriFlow = mainViewModel.selectedImageUri,
                signUp = { name, surname, phoneNumber, email, password, imageUri ->
                    authViewModel.signUp(name, surname, phoneNumber, email, password, imageUri)
                }
            )
        }
        composable(Routes.homeScreen) {

        }
    }
}