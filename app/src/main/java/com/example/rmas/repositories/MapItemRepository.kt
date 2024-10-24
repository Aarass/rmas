package com.example.rmas.repositories

import android.net.Uri
import android.util.Log
import androidx.compose.animation.core.snap
import com.example.rmas.models.MapItem
import com.example.rmas.models.UserTag
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue.serverTimestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class MapItemRepository {
    private val collectionName = "mapItems"
    private val db: FirebaseFirestore = Firebase.firestore

    fun getAllMapItems(fn: (List<MapItem>) -> Unit) {
        db.collection(collectionName).addSnapshotListener { snapshotN, _ ->
            snapshotN?.let { snapshot ->
                val list = snapshot.documents.map { document ->
                    MapItem(
                        id = document.id
                    )
                }
                fn(list)
            }
        }
    }

    suspend fun addNewMapItem(
        images: Collection<Uri>,
        tags: Collection<UserTag>,
        title: String,
        description: String,
        authorUid: String,
        location: LatLng
    ) {
        db.collection("mapItems").add(
            hashMapOf(
                "author" to authorUid,
                "location" to location,
                "title" to title,
                "description" to description,
                "tags" to tags.filter { userTag -> userTag.selected }.map {userTag -> userTag.tag.id},
                "images" to images,
                "createdAt" to serverTimestamp()
            )
        ).await()
    }
}