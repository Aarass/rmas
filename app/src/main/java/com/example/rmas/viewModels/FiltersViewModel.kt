package com.example.rmas.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rmas.models.Filters
import com.example.rmas.models.Tag
import com.example.rmas.models.User
import com.example.rmas.models.UserTag
import com.example.rmas.repositories.ServiceLocator
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class FiltersViewModel: ViewModel() {
    private val allTags = ServiceLocator.tagRepository.getAllTags()

    var currentFilters = Filters(activeTags = allTags.map { it.id })
        private set

    private val authorsQuery = MutableSharedFlow<String>()
    private val queriedAuthors = mutableStateListOf<User>()
    var isQueryingAuthors by mutableStateOf(false)
    var authorsQueryState by mutableStateOf("")
        private set


    init {
        viewModelScope.launch {
            authorsQuery.debounce(500).collect { query ->
                queriedAuthors.clear()
                if (query.isNotEmpty()) {
                    isQueryingAuthors = true
                    try {
                        delay(2000)
                        val elements = ServiceLocator.userRepository.getUsersBySubstring(query)
                        queriedAuthors.addAll(elements)
                    } finally {
                        isQueryingAuthors = false
                    }
                }
            }
        }
    }

    fun getQueriedAuthorsState(): List<User> {
        return queriedAuthors
    }

    fun authorsQueryChanged(newQuery: String) {
        queriedAuthors.clear()
        authorsQueryState = newQuery
        viewModelScope.launch {
            authorsQuery.emit(newQuery)
        }
    }


    fun getTagById(id: String): Tag? {
        return allTags.find {
            it.id == id
        }
    }

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

        currentFilters = currentFilters.copy(
            activeTags = _userTags.values.filter { it.selected }.map { it.tag.id }
        )
    }

    fun setFilters(author: User?, dateRange: Pair<Long?, Long?>, distance: Float?) {
        currentFilters = currentFilters.copy(
            author = author, dateRange = dateRange, locationRange = distance
        )
    }
}
