package com.example.rmas.viewModels

import androidx.lifecycle.ViewModel
import com.example.rmas.models.Tag
import com.example.rmas.models.UserTag
import com.example.rmas.repositories.ServiceLocator

class FiltersViewModel: ViewModel() {
    private val allTags = ServiceLocator.tagRepository.getAllTags()

    private val _userTags = mutableMapOf<String, UserTag>().apply {
        this.putAll(
            allTags.map {
                it.id to UserTag(it)
            }
        )
    }

    val userTags = _userTags as Map<String, UserTag>

    fun setTagValue(id: String, value: Boolean) {
        _userTags[id] = _userTags[id]?.copy(selected = value) ?: throw Exception("There is no tag with passed id")
    }

    fun getTagById(id: String): Tag? {
        return allTags.find {
            it.id == id
        }
    }
}
