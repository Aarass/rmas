package com.example.rmas.viewModels

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import com.example.rmas.models.UserTag
import com.example.rmas.repositories.ServiceLocator
import okhttp3.internal.toImmutableMap

class AddMapItemViewModel: ViewModel() {
    private val allTags = ServiceLocator.tagRepository.getAllTags()

    private val _selectedTags = mutableStateMapOf<String, UserTag>().apply {
        this.putAll(
            allTags.map {
                it.id to UserTag(it, false)
            }
        )
    }

    val selectedTags = _selectedTags as Map<String, UserTag>

    fun setTagValue(id: String, value: Boolean) {
        _selectedTags[id] = _selectedTags[id]?.copy(selected = value) ?: throw Exception("There is no tag with passed id")
    }

    fun clearAll() {
        _selectedTags.clear()
        _selectedTags.putAll(
            allTags.map {
                it.id to UserTag(it, false)
            }
        )
    }
}