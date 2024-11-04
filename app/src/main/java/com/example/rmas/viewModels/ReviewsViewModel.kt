package com.example.rmas.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rmas.models.Review
import com.example.rmas.repositories.ServiceLocator
import kotlinx.coroutines.launch

class ReviewsViewModel: ViewModel() {
    suspend fun getReviewsForMapItem(id: String): List<Review> {
        return ServiceLocator.reviewRepository.getReviewsForMapItem(id)
    }

    fun createReview(userId: String, mapItemId: String, rating: Int, comment: String?, callback: () -> Unit) {
        viewModelScope.launch {
            ServiceLocator.reviewRepository.postReview(userId, mapItemId, rating, comment)
        }
    }
}