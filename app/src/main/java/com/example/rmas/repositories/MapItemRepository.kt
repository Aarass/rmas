package com.example.rmas.repositories

import android.net.Uri
import android.util.Log
import androidx.compose.animation.core.snap
import com.example.rmas.models.MapItem
import com.example.rmas.models.Tag
import com.example.rmas.models.UserTag
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue.serverTimestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.getField
import kotlinx.coroutines.tasks.await

class MapItemRepository {
    private val collectionName = "mapItems"

//    collectionRef
//    .where('name', '>=', queryText)
//    .where('name', '<=', queryText+ '\uf8ff')

    fun getAllMapItems(fn: (List<MapItem>) -> Unit) {
        Firebase.firestore.collection(collectionName).addSnapshotListener { maybeSnapshot, _ ->
            try {
                maybeSnapshot?.let { snapshot ->
                    val list = snapshot.documents.map { document ->
                        MapItem(
                            id = document.id,
                            authorUid = document.getField<String>("authorUid") ?: "",
                            title = document.getField<String>("title") ?: "",
                            description = document.getField<String>("description") ?: "",
                            location = LatLng(
                                document.getField<Double>("location.latitude") ?: 0.0,
                                document.getField<Double>("location.longitude") ?: 0.0
                            ),
                            tags = document.get("tags") as List<String>,
                            images = document.get("images") as List<String>,
                        )
                    }

                    fn(list)
                }
            } catch (err: Exception) {
                Log.e("myerr", err.toString())
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
        Firebase.firestore.collection("mapItems").add(
            hashMapOf(
                "authorUid" to authorUid,
//                "location" to location,
                "location" to hashMapOf(
                    "latitude" to location.latitude,
                    "longitude" to location.longitude,
                ),
                "title" to title,
                "description" to description,
                "tags" to tags.filter { userTag -> userTag.selected }.map {userTag -> userTag.tag.id},
                "images" to images,
                "createdAt" to serverTimestamp()
            )
        ).await()
    }
}