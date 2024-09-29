package com.example.rmas.repositories

object ServiceLocator {
    public val userRepository = UserRepository()
    public val imageRepository = ImageRepository()
    public val tagRepository = TagRepository()
}