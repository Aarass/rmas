package com.example.rmas.models

import com.google.firebase.firestore.DocumentId

class Review(
    @DocumentId
    val id: String,
    val rating: Int,
    val comment: String,
)