package com.example.rmas.errors

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ErrorHandler(private val coroutineScope: CoroutineScope) {
    private val _errors = MutableSharedFlow<String>()
    val errors = _errors.asSharedFlow()

    public fun showError(error: Exception) {
        val message = error.message ?: "Error without message"

        Log.e("AuthViewModel", message)
        coroutineScope.launch {
            _errors.emit(message)
        }
    }
}