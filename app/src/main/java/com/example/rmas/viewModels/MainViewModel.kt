package com.example.rmas.viewModels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {
    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri = this._selectedImageUri.asStateFlow()

    fun setSelectedImageUri(uri: Uri) {
        viewModelScope.launch {
            _selectedImageUri.emit(uri)
        }
    }

    fun clearSelectedImageUri() {
        viewModelScope.launch {
            _selectedImageUri.emit(null)
        }
    }

    private val _takeImage = MutableSharedFlow<Unit>()
    val takeImageEvent = _takeImage.asSharedFlow()

    fun takePicture() {
        viewModelScope.launch {
            _takeImage.emit(Unit)
        }
    }
}