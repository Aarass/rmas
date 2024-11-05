package com.example.rmas.models

import com.google.firebase.firestore.DocumentId

data class Review(
    @DocumentId
    val userId: String,
    val rating: Int,
    val comment: String?,
)