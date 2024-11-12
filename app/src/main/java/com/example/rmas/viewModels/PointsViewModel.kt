package com.example.rmas.viewModels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rmas.models.Points
import com.example.rmas.repositories.ServiceLocator
import kotlinx.coroutines.launch

class PointsViewModel: ViewModel() {
    private var _points = mutableStateListOf<Points>()
    val points = _points as List<Points>

    fun getPointsForUser(userId: String) {
        viewModelScope.launch {
            val points = ServiceLocator.pointsRepository.getPointsForUser(userId)
            _points.clear()
            _points.addAll(points)
        }
    }
}