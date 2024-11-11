package com.example.rmas.viewModels

import android.content.ContentResolver
import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rmas.models.MapItem
import com.example.rmas.models.Tag
import com.example.rmas.models.UserTag
import com.example.rmas.repositories.ServiceLocator
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random

class AddMapItemViewModel: ViewModel() {
    private val allTags = mutableStateListOf<Tag>()

    private val _selectedTags = mutableStateMapOf<String, UserTag>()
    val selectedTags = _selectedTags as Map<String, UserTag>

    init {
        viewModelScope.launch {
            allTags.addAll(ServiceLocator.tagRepository.getAllTags())

            _selectedTags.putAll(
                allTags.map { tag ->
                    tag.id to UserTag(tag, false)
                }
            )
        }
    }

    fun setTagValue(id: String, value: Boolean) {
        _selectedTags[id] = _selectedTags[id]?.copy(selected = value) ?: throw Exception("There is no tag with passed id")
    }

    fun resetTags() {
        _selectedTags.forEach { entry ->
            entry.value.selected = false
        }
    }

    private val _images = mutableStateListOf<Uri>()
    val images = _images as List<Uri>

    fun addImage(uri: Uri) {
        _images.add(uri)
    }

    fun removeImage(index: Int) {
        _images.removeAt(index)
    }

    fun resetImages() {
        _images.clear()
    }

    suspend fun uploadNewMapItem(
        images: Collection<Uri>,
        tags: Collection<UserTag>,
        title: String,
        description: String,
        authorUid: String,
        location: LatLng,
        contentResolver: ContentResolver,
    ): MapItem {
        val urlsInCloud = images.map { image ->
            ServiceLocator.imageRepository.uploadImage(UUID.randomUUID().toString(), image, contentResolver)
        }
        val randomizedLocation = LatLng(
            location.latitude + Random.nextDouble(-0.005, 0.005),
            location.longitude + Random.nextDouble(-0.005, 0.005),
        )
        return ServiceLocator.mapItemRepository.addNewMapItem(urlsInCloud, tags, title, description, authorUid, randomizedLocation)
    }
}