package com.example.rmas.repositories

object ServiceLocator {
    val userRepository = UserRepository()
    val imageRepository = ImageRepository()
    val tagRepository = TagRepository()
    val mapItemRepository = MapItemRepository()
    val reviewRepository = ReviewRepository()
    val pointsRepository = PointsRepository()
}