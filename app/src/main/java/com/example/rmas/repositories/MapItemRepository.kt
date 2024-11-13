package com.example.rmas.repositories

import android.location.Location
import android.net.Uri
import android.util.Log
import androidx.compose.animation.core.snap
import com.example.rmas.models.Filters
import com.example.rmas.models.MapItem
import com.example.rmas.models.UserTag
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue.serverTimestamp
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import java.util.Date
import kotlin.math.PI
import kotlin.math.cos

class MapItemRepository {
    private val collectionName = "mapItems"

    suspend fun getMapItems(filters: Filters, location: Location): List<MapItem> {
        Log.i("jkl", filters.toString())


        var query = Firebase.firestore.collection(collectionName) as Query

        filters.author?.let { query = query.where(Filter.equalTo("authorUid", it.uid)) }
        query = query.where(Filter.greaterThanOrEqualTo("createdAt", Date(filters.dateRange.first ?: 0)))
        query = query.where(Filter.lessThanOrEqualTo("createdAt", filters.dateRange.second?.run { Date(this) } ?: Date() ))
        query = query.where(Filter.arrayContainsAny("tags", filters.activeTags.ifEmpty { listOf("-99") }))

        query = filters.locationRange?.let { radius ->
            val latitudeRad = location.latitude * PI / 180.0
            val deltaLatitude = radius / 111f
            val deltaLongitude = radius / 111f * cos(latitudeRad)

            val top =  location.latitude - deltaLatitude
            val bottom =  location.latitude + deltaLatitude
            val left = location.longitude - deltaLongitude
            val right = location.longitude + deltaLongitude

            Log.i("jkl", "$top, $bottom, $left, $right")

            query.where(Filter.and(
                Filter.greaterThanOrEqualTo("location.latitude", top),
                Filter.lessThanOrEqualTo("location.latitude", bottom),
                Filter.greaterThanOrEqualTo("location.longitude", left),
                Filter.lessThanOrEqualTo("location.longitude", right),
            ))
        } ?: query.where(Filter.and(
                Filter.greaterThanOrEqualTo("location.latitude", -90),
                Filter.lessThanOrEqualTo("location.latitude", 90),
                Filter.greaterThanOrEqualTo("location.longitude", -180),
                Filter.lessThanOrEqualTo("location.longitude", 180),
            ))

        return filters.locationRange?.let { radius ->
            query.limit(100).get().await().documents.map { MapItem.from(it) }.filter { mapItem ->
                val res = FloatArray(3)
                Location.distanceBetween(
                    location.latitude,
                    location.longitude,
                    mapItem.location.latitude,
                    mapItem.location.longitude,
                    res
                )
                val d = res[0]

                Log.i("jkl", d.toString())

                d <= radius * 1000
            }
        } ?: query.limit(100).get().await().documents.map { MapItem.from(it) }

    }

    suspend fun getAllMapItemsInRange(location: Location, maxDistance: Float): List<MapItem> {
        Log.i("iop", "in getAll")
        var query = Firebase.firestore.collection(collectionName) as Query

        val latitudeRad = location.latitude * PI / 180.0
        val deltaLatitude = maxDistance / 111f
        val deltaLongitude = maxDistance / 111f * cos(latitudeRad)

        val top =  location.latitude - deltaLatitude
        val bottom =  location.latitude + deltaLatitude
        val left = location.longitude - deltaLongitude
        val right = location.longitude + deltaLongitude

        query = query.where(Filter.and(
            Filter.greaterThanOrEqualTo("location.latitude", top),
            Filter.lessThanOrEqualTo("location.latitude", bottom),
            Filter.greaterThanOrEqualTo("location.longitude", left),
            Filter.lessThanOrEqualTo("location.longitude", right),
        ))

        Log.i("iop", "before query")
        val snapshots =query.limit(100).get().await().documents
        Log.i("iop", "after query, $snapshots")
        return snapshots.map { MapItem.from(it) }.filter { mapItem ->
            val res = FloatArray(3)
            Location.distanceBetween(
                location.latitude,
                location.longitude,
                mapItem.location.latitude,
                mapItem.location.longitude,
                res
            )
            val d = res[0]

            Log.i("jkl", d.toString())

            d <= maxDistance * 1000
        }


//        Firebase.firestore.collection(collectionName).addSnapshotListener { maybeSnapshot, _ ->
//            try {
//                maybeSnapshot?.let { snapshot ->
//                    callback(snapshot.documents.map { MapItem.from(it) })
//                }
//            } catch (err: Exception) {
//                Log.e("myerr", err.toString())
//            }
//        }
    }

    suspend fun addNewMapItem(
        images: Collection<Uri>,
        tags: Collection<UserTag>,
        title: String,
        description: String,
        authorUid: String,
        location: LatLng
    ): MapItem {
        val documentReference = Firebase.firestore.collection("mapItems").add(
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

        return MapItem.from(documentReference.get().await())
    }
}