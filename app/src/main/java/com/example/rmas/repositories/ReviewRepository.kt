package com.example.rmas.repositories

import com.example.rmas.models.Review
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.getField
import kotlinx.coroutines.tasks.await

class ReviewRepository {
    suspend fun getReviewsForMapItem(id: String): List<Review> {
        val snapshot = Firebase.firestore.collection("mapItems").document(id).collection("reviews").get().await()
        return snapshot.documents.map { document ->
            Review(
                userId = document.id,
                rating = document.getField<Int>("rating") ?: 3,
                comment = document.getField<String>("comment"),
            )
        }
    }

    suspend fun postReview(userId: String, mapItemId: String, rating: Int, comment: String?): Review {
        Firebase.firestore
            .collection("mapItems").document(mapItemId)
            .collection("reviews").document(userId)
            .set(
                hashMapOf(
                    "rating" to rating,
                    "comment" to comment
                )
            ).await()

        return reviewFromDocument(
            Firebase.firestore
                .collection("mapItems").document(mapItemId)
                .collection("reviews").document(userId)
                .get().await()
        )
    }


    private fun reviewFromDocument(document: DocumentSnapshot): Review {
        return Review(
            userId = document.id,
            rating = document.getField<Int>("rating") ?: 3,
            comment = document.getField<String>("comment"),
        )
    }
}