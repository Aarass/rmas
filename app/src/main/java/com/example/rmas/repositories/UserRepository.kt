package com.example.rmas.repositories

import android.net.Uri
import com.example.rmas.models.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue.serverTimestamp
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class UserRepository {
    private var auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore = Firebase.firestore

    suspend fun getLeaderboard(): List<User> {
        return db.collection("users").orderBy("points", Query.Direction.DESCENDING).get().await().map { User.from(it) }
    }

    suspend fun getUser(userUid: String): User? {
        val document = db.collection("users").document(userUid).get().await()
        
        if (!document.exists()) {
            return null
        }

        return User.from(document)
    }

    suspend fun getUsersBySubstring(query: String): List<User> {
        val res = db.collection("users").where(
            Filter.and(
                Filter.greaterThanOrEqualTo("fullName", query),
                Filter.lessThanOrEqualTo("fullName", "$query~"), // '\uf8ff'
            )
        ).limit(5).get().await()
        return res.documents.map { User.from(it) }
    }

    suspend fun createUser(email: String, password: String): FirebaseUser {
        val user = auth.createUserWithEmailAndPassword(email, password).await().user ?: throw Exception("Couldn't create user")

        db.collection("users").document(user.uid).set(hashMapOf(
            "createdAt" to serverTimestamp()
        ))

        return user
    }

    suspend fun setUserInfo(user: FirebaseUser, name: String, surname: String, phoneNumber: String) {
        db.runBatch { batch ->
            val documentRef = db.collection(("users")).document(user.uid);

            batch
                .update(documentRef, "name", name)
                .update(documentRef, "surname", surname)
                .update(documentRef, "phoneNumber", phoneNumber)
        }.await()
    }

    suspend fun setUserProfileImage(user: FirebaseUser, imageUrl: Uri) {
        db.collection(("users")).document(user.uid).update("imageUrl", imageUrl.toString())
    }
}