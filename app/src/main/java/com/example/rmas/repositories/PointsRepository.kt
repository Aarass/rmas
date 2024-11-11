package com.example.rmas.repositories

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

// ID = [r | c] _ [me | ot] _ otherId _ mapItemId
class PointsRepository {



    suspend fun addPoints(userId: String, id: String, message: String, value: Float) {
        val userDoc = Firebase.firestore.collection("users").document(userId)

        Firebase.firestore.runTransaction { transaction ->
            val newPoints = transaction.get(userDoc).getDouble("points")!! + value
            Log.i("uio", newPoints.toString())
            transaction.update(userDoc, "points", newPoints)

            // Success
            null
        }
        Firebase.firestore.collection("users").document(userId).collection("points").document(id).set(
            hashMapOf(
                "message" to message,
                "value" to value,
            )
        ).await()
    }

    suspend fun removePoints(userId: String, id: String) {
        val userDoc = Firebase.firestore.collection("users").document(userId)
        val pointsDoc = userDoc.collection("points").document(id)

        try {
            Firebase.firestore.runTransaction { transaction ->
                val userSnapshot = transaction.get(userDoc)
                val pointsSnapshot = transaction.get(pointsDoc)
                val oldPoints = userSnapshot.getDouble("points")!!
                val points = pointsSnapshot.getDouble("value")!!
                val newPoints = oldPoints - points
                Log.i("uio", "$newPoints = $oldPoints - $points")
                transaction.update(userDoc, "points", newPoints)

                // Success
                null
            }.await()
            Firebase.firestore.collection("users").document(userId).collection("points").document(id).delete().await()
        } catch (_: Exception) {

        }
    }
}