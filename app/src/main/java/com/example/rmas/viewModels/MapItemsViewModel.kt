package com.example.rmas.viewModels

import android.location.Location
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rmas.models.Filters
import com.example.rmas.models.MapItem
import com.example.rmas.repositories.ServiceLocator
import kotlinx.coroutines.launch

class MapItemsViewModel: ViewModel() {
    private val mapItems = mutableStateListOf<MapItem>()

    private var selectedItem = mutableStateOf<MapItem?>(null)

    fun queryMapItems(filters: Filters, usersLocation: Location) {
        viewModelScope.launch {
            val items = ServiceLocator.mapItemRepository.getMapItems(filters, usersLocation)
            mapItems.clear()
            mapItems.addAll(items)
        }
    }

    fun addNewMapItem(item: MapItem) {
        mapItems.add(item)
    }

    fun getMapItems(): List<MapItem> {
        return mapItems
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