package com.example.rmas.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.getField

data class User(
    val uid: String,
    val fullName: String,
    val phoneNumber: String,
    val imageUrl: String,
) {
    companion object {
        fun from(document: DocumentSnapshot): User {
            return User(
                uid = document.id,
                fullName = document.getField("fullName") ?: "",
                phoneNumber = document.getField("phoneNumber") ?: "",
                imageUrl = document.getField("imageUrl") ?: "",
            )
        }
    }
}
