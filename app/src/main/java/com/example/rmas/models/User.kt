package com.example.rmas.models

import com.google.firebase.firestore.DocumentId

data class User(
    val uid: String,
    val name: String,
    val surname: String,
    val phoneNumber: String,
    val imageUrl: String,
)
