package com.example.rmas.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rmas.models.MapItem
import com.example.rmas.models.Review
import com.example.rmas.models.User
import com.example.rmas.repositories.ServiceLocator
import kotlinx.coroutines.launch

class ReviewsViewModel: ViewModel() {
    suspend fun getReviewsForMapItem(id: String): List<Review> {
        return ServiceLocator.reviewRepository.getReviewsForMapItem(id)
    }

    // ID = [r | c] _ [me | ot] _ otherId _ mapItemId
    private suspend fun takeAwayPointsIfReviewExist(reviewId: String, mapItemId: String, authorId: String) {
        val reviewDoc = ServiceLocator.reviewRepository.getReview(mapItemId, reviewId)
        if (reviewDoc.exists()) {
            // reviewId = userId = Reviewer.Id
            // id review-a je zapravo id user-a koji ga je napravio
            // a sam review nalazi se u subkolekciji mapItem-a
            val review = Review.from(reviewDoc)
            val pointsIdSuffix = "${reviewId}_${mapItemId}"

            ServiceLocator.pointsRepository.removePoints(userId = reviewId, "r_me_${pointsIdSuffix}")
            ServiceLocator.pointsRepository.removePoints(userId = authorId, "r_ot_${pointsIdSuffix}")
            if (review.comment != null) {
                ServiceLocator.pointsRepository.removePoints(userId = reviewId, "c_me_${pointsIdSuffix}")
                ServiceLocator.pointsRepository.removePoints(userId = authorId, "c_ot_${pointsIdSuffix}")
            }
        }
    }

    // ID = [r | c] _ [me | ot] _ otherId _ mapItemId
    private suspend fun addRatingPoints(authorId: String, reviewerId: String, mapItemId: String, rating: Int) {
        ServiceLocator.pointsRepository.addPoints(
            authorId,
            "r_ot_${reviewerId}_${mapItemId}",
            "User rated your object with $rating stars",
            1f + rating / 2f - 1f
        )
        ServiceLocator.pointsRepository.addPoints(
            reviewerId,
            "r_me_${reviewerId}_${mapItemId}",
            "You rated an object with $rating stars",
            1f
        )
    }

    private suspend fun addCommentPoints(authorId: String, reviewerId: String, mapItemId: String) {
        ServiceLocator.pointsRepository.addPoints(
            authorId,
            "c_ot_${reviewerId}_${mapItemId}",
            "User left comment on one of your objects",
            2f
        )
        ServiceLocator.pointsRepository.addPoints(
            reviewerId,
            "c_me_${reviewerId}_${mapItemId}",
            "You commented on an object",
            1f
        )
    }

    fun upsertReview(userId: String, mapItemId: String, authorId: String, rating: Int, comment: String?, callback: (Review) -> Unit) {
        viewModelScope.launch {
            // ---------------------------------  |
            // -------------------------------- / | \
            // ------------------------------ \/ \/ \/
            takeAwayPointsIfReviewExist(reviewId = userId, mapItemId, authorId)
            // -----------------------------------------------------------------  |
            // ---------------------------------------------------------------- / | \
            // -------------------------------------------------------------- \/ \/ \/
            val review = ServiceLocator.reviewRepository.upsertReview(id = userId, mapItemId, rating, comment)

            callback(review)
            addRatingPoints(authorId, userId, mapItemId, rating)
            if (review.comment != null){
                addCommentPoints(authorId, userId, mapItemId)
            }
        }
    }

    fun deleteReview(mapItem: MapItem, review: Review) {
        viewModelScope.launch {
            takeAwayPointsIfReviewExist(review.userId, mapItem.id, mapItem.authorUid)
            ServiceLocator.reviewRepository.deleteReview(mapItem.id, review.userId)
        }
    }


    suspend fun getUserById(id: String): User? {
        return ServiceLocator.userRepository.getUser(id)
    }
}