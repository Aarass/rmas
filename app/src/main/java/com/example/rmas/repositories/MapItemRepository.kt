package com.example.rmas.repositories

import android.net.Uri
import android.util.Log
import com.example.rmas.models.Filters
import com.example.rmas.models.MapItem
import com.example.rmas.models.UserTag
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue.serverTimestamp
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class MapItemRepository {
    private val collectionName = "mapItems"

    suspend fun getMapItems(filters: Filters): List<MapItem> {
        val query = Firebase.firestore.collection(collectionName).where(
            Filter.and(
                filters.authorId?.let { Filter.equalTo("authorUid", it) },
            )
        )

        return query.get().await().documents.map { MapItem.from(it) }
    }

    fun getAllMapItems(callback: (List<MapItem>) -> Unit) {
        Firebase.firestore.collection(collectionName).addSnapshotListener { maybeSnapshot, _ ->
            try {
                maybeSnapshot?.let { snapshot ->
                    callback(snapshot.documents.map { MapItem.from(it) })
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