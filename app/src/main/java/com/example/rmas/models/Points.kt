package com.example.rmas.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.getField

data class Points(
    @DocumentId
    val documentId: String,
    val message: String,
    val value: Int,
) {
    companion object {
        fun from(document: DocumentSnapshot): Points {
            return Points(
                documentId = document.id,
                message = document.getField<String>("message") ?: "",
                value = document.getField<Int>("value") ?: 0,
            )
        }
    }
}