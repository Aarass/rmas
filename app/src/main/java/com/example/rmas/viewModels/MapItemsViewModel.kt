package com.example.rmas.viewModels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rmas.models.MapItem
import com.example.rmas.repositories.ServiceLocator
import kotlinx.coroutines.launch

class MapItemsViewModel: ViewModel() {
    private val mapItemRepository = ServiceLocator.mapItemRepository
    private val allMapItems: SnapshotStateList<MapItem> = mutableStateListOf<MapItem>()

    private var selectedItem = mutableStateOf<MapItem?>(null)


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

    fun getSelectedItem(): State<MapItem?> {
        return selectedItem
    }

    fun selectItem(item: MapItem) {
        selectedItem.value = item
    }

    fun deselectItem() {
        selectedItem.value = null
    }
}