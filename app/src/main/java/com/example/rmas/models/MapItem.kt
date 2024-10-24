package com.example.rmas.models

import com.google.firebase.firestore.DocumentId

data class MapItem(
    @DocumentId
    val id: String,
)
