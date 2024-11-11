package com.example.rmas.viewModels

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.rmas.models.Filters
import com.example.rmas.models.MapItem
import com.example.rmas.repositories.ServiceLocator
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MapItemsViewModel(private val locationProvider: FusedLocationProviderClient): ViewModel() {
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

    companion object {
        val LOCATION_PROVIDER= object : CreationExtras.Key<FusedLocationProviderClient> {}

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val locationProvider = this[LOCATION_PROVIDER] as FusedLocationProviderClient
                MapItemsViewModel(locationProvider)
            }
        }
    }
}