package com.example.rmas.viewModels

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rmas.models.User
import com.example.rmas.repositories.ServiceLocator
import kotlinx.coroutines.launch

class LeaderboardViewModel: ViewModel() {
    private val _leaderboard = mutableStateListOf<User>()
    val leaderboard = _leaderboard as List<User>

    fun fetch() {
        viewModelScope.launch {
            val tmp = ServiceLocator.userRepository.getLeaderboard()
            _leaderboard.clear()
            _leaderboard.addAll(tmp)
        }
    }
}