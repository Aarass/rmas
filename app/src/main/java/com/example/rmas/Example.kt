package com.example.rmas

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser

class Example: ViewModel() {
    private var _user by mutableStateOf<FirebaseUser?>(null)

    val user: FirebaseUser?
        get() {
            return this._user
        }

    fun setUser(user: FirebaseUser) {
        this._user = user
    }
}