package com.example.rmas.viewModels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rmas.errors.ErrorHandler
import com.example.rmas.repositories.ServiceLocator
import kotlinx.coroutines.launch

class AuthViewModel(): ViewModel() {
    private val errorHandler = ErrorHandler(coroutineScope = viewModelScope)
    val errors = errorHandler.errors

    private val userRepository = ServiceLocator.userRepository;
    private val imageRepository = ServiceLocator.imageRepository;

    fun signIn(username: String, password: String) {
        // TODO
    }

    fun signUp(name: String, surname: String, phoneNumber: String, email: String, password: String, imageUri: Uri) {
        viewModelScope.launch {
            try {
                userRepository.createUser(email, password)?.also { user ->
                    imageRepository.uploadImage(imageUri)

                    userRepository.addUserInfo(
                        user = user,
                        name = name,
                        surname = surname,
                        phoneNumber = phoneNumber
                    )
                    
                    userRepository.addUserImage(
                        user = user,
                        imageUri = imageUri
                    )
                } ?: throw Exception("Couldn't create user")
            } catch (e: Exception) {
                errorHandler.showError(e)
            }
        }
    }
}