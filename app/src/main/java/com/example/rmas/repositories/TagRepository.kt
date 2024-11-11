package com.example.rmas.repositories

import com.example.rmas.models.Tag
import com.example.rmas.models.UserTag
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class TagRepository {
    suspend fun getAllTags(): List<Tag> {
        return Firebase.firestore.collection("tags").get().await().map { Tag.from(it) }
    }
}