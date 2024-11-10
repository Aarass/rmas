package com.example.rmas.viewModels

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rmas.enums.AuthStatus
import com.example.rmas.errors.ErrorHandler
import com.example.rmas.models.User
import com.example.rmas.repositories.ServiceLocator
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel: ViewModel() {
    private var auth: FirebaseAuth = Firebase.auth

    private val errorHandler = ErrorHandler(coroutineScope = viewModelScope)
    val errors = errorHandler.errors

    private val userRepository = ServiceLocator.userRepository
    private val imageRepository = ServiceLocator.imageRepository

    private val _isSigningUp = MutableStateFlow(false)
    val isSigningUp = _isSigningUp.asStateFlow()

    private val _isSigningIn= MutableStateFlow(false)
    val isSigningIn = _isSigningIn.asStateFlow()

    private val _authStatus = MutableStateFlow(AuthStatus.NotLogedIn)
    val onAuthStatusChange = _authStatus.asSharedFlow()
        //.drop(1)
    val authStatus = _authStatus.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()


    fun tryToRestoreSession() {
        val user = auth.currentUser;
        if (user != null) {
            _authStatus.value = AuthStatus.LogedIn
            viewModelScope.launch {
                _currentUser.value = userRepository.getUser(user.uid)
            }
        }
        Log.i("auth", auth.currentUser?.uid ?: "no user")
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            setIsSigningIn(true)

            try {
                auth.signInWithEmailAndPassword(email, password).await()
                _authStatus.value = AuthStatus.LogedIn
                viewModelScope.launch {
                    _currentUser.value = userRepository.getUser(auth.currentUser!!.uid)
                }

            } catch (e: Exception) {
                errorHandler.showError(e)
            } finally {
                setIsSigningIn(false)
            }
        }
    }

    fun signUp(name: String, surname: String, phoneNumber: String, email: String, password: String, imageUri: Uri, contentResolver: ContentResolver) {
        viewModelScope.launch {
            setIsSigningUp(true)
            try {
                val user = userRepository.createUser(email, password)

                listOf(
                    async {
                        userRepository.setUserInfo(
                            user = user,
                            name = name,
                            surname = surname,
                            phoneNumber = phoneNumber
                        )
                    },
                    async {
                        val uploadedImageUrl = imageRepository.uploadImage("user_${user.uid}", imageUri, contentResolver)
                        userRepository.setUserProfileImage(
                            user = user,
                            imageUrl = uploadedImageUrl
                        )

                        _currentUser.value = User(
                            uid = user.uid,
                            fullName = "$name $surname",
                            phoneNumber = phoneNumber,
                            imageUrl = uploadedImageUrl.toString(),
                        )
                    }
                ).awaitAll()

                _authStatus.value = AuthStatus.LogedIn
            } catch (e: Exception) {
                errorHandler.showError(e)
            } finally {
                setIsSigningUp(false)
            }
        }
    }

    private fun setIsSigningIn(value: Boolean) {
        viewModelScope.launch {
            _isSigningIn.emit(value)
        }
    }

    private fun setIsSigningUp(value: Boolean) {
        _isSigningUp.value = value
    }

    fun signOut() {
        auth.signOut()
        _authStatus.value = AuthStatus.NotLogedIn
    }
}