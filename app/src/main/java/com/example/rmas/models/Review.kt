package com.example.rmas.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.getField

data class Review(
    @DocumentId
    val userId: String,
    val rating: Int,
    val comment: String?,
) {
    companion object {
        fun from(document: DocumentSnapshot): Review {
            return Review(
                userId = document.id,
                rating = document.getField<Int>("rating") ?: 3,
                comment = document.getField<String>("comment"),
            )
        }
    }
}