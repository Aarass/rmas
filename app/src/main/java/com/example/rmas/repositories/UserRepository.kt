package com.example.rmas.repositories

import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await

class UserRepository {
    private var auth: FirebaseAuth = Firebase.auth

    suspend fun createUser(email: String, password: String): FirebaseUser? {
        return auth.createUserWithEmailAndPassword(email, password).await().user
    }

    suspend fun addUserInfo(user: FirebaseUser,name: String, surname: String, phoneNumber: String) {
        throw NotImplementedError()
    }

    suspend fun addUserImage(user: FirebaseUser, imageUri: Uri) {
        throw NotImplementedError()
    }
}