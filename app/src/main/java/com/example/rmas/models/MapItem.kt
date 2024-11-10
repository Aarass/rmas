package com.example.rmas.models

import com.example.rmas.viewModels.MapItemsViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.getField

data class MapItem(
    @DocumentId
    val id: String,
    val authorUid: String,
    val location: LatLng,
    val title: String,
    val description: String,
    val tags: List<String>,
    val images: List<String>,
) {
    companion object {
        @Suppress("UNCHECKED_CAST")
        fun from(document: DocumentSnapshot): MapItem {
            return MapItem(
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
    }
}
