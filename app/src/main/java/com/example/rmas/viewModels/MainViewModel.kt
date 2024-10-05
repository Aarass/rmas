package com.example.rmas.viewModels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {
    private val _newImageUri = MutableSharedFlow<Uri>()
    val newImageUri = this._newImageUri.asSharedFlow()

    fun setSelectedImageUri(uri: Uri) {
        viewModelScope.launch {
            _newImageUri.emit(uri)
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