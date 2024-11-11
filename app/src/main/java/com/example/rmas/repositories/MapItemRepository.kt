package com.example.rmas.repositories

import android.net.Uri
import android.util.Log
import com.example.rmas.models.Filters
import com.example.rmas.models.MapItem
import com.example.rmas.models.UserTag
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue.serverTimestamp
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import java.util.Date

class MapItemRepository {
    private val collectionName = "mapItems"

    suspend fun getMapItems(filters: Filters): List<MapItem> {
        var query = Firebase.firestore.collection(collectionName) as Query


        // Todo prepraviti da se uvek koriste svi filteri da ne bi moralo da se
        // pravi poseban indeks za svaku kombinaciju filtera
        // kapiram da za authorId mozda nece moci

        // Takodje resiti kako dolazi korisnikova lokacije dovde
        // !!! To je mozna i najhitnije !!!

        filters.authorId?.let { query = query.where(Filter.equalTo("authorUid", it)) }
        query = query.where(Filter.greaterThanOrEqualTo("createdAt", Date(filters.dateRange.first ?: 0)))
        query = query.where(Filter.lessThanOrEqualTo("createdAt", filters.dateRange.second?.run { Date(this) } ?: Date() ))
        filters.locationRange?.let {
            query = query.where(Filter.and(
                Filter.greaterThanOrEqualTo("location.latitude", 42f),
                Filter.lessThanOrEqualTo("location.latitude", 44f),
                Filter.greaterThanOrEqualTo("location.longitude", 20f),
                Filter.lessThanOrEqualTo("location.longitude", 22f),
            ))
        }
        if (filters.activeTags.isNotEmpty()) {
            filters.activeTags.let {
                query = query.where(Filter.arrayContainsAny("tags", it))
            }
        }

        return query.limit(100).get().await().documents.map { MapItem.from(it) }
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