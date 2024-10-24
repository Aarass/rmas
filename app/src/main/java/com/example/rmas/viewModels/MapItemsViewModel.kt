package com.example.rmas.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rmas.models.MapItem
import com.example.rmas.repositories.ServiceLocator
import kotlinx.coroutines.launch

class MapItemsViewModel: ViewModel() {
    private val mapItemRepository = ServiceLocator.mapItemRepository
    private val allMapItems = mutableListOf<MapItem>()

    init {
        viewModelScope.launch {
            mapItemRepository.getAllMapItems { list ->
                allMapItems.clear()
                allMapItems.addAll(list)
            }
        }
    }

    fun getMapItems(): List<MapItem> {
        return allMapItems
    }
}