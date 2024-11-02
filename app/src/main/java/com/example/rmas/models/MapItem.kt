package com.example.rmas.models

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.DocumentId

data class MapItem(
    @DocumentId
    val id: String,
    val authorUid: String,
    val location: LatLng,
    val title: String,
    val description: String,
    val tags: List<String>,
    val images: List<String>,
)
