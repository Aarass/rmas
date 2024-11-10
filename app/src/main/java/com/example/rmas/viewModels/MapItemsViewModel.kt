package com.example.rmas.viewModels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rmas.models.Filters
import com.example.rmas.models.MapItem
import com.example.rmas.repositories.ServiceLocator
import kotlinx.coroutines.launch

class MapItemsViewModel: ViewModel() {
    private val mapItems: SnapshotStateList<MapItem> = mutableStateListOf<MapItem>()

    private var selectedItem = mutableStateOf<MapItem?>(null)

    fun onFiltersChanged(filters: Filters) {
        Log.i("jkl", "Promena, $filters")
        viewModelScope.launch {
            mapItems.clear()
            mapItems.addAll(ServiceLocator.mapItemRepository.getMapItems(filters))
        }
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