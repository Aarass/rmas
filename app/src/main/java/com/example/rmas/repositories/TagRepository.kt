package com.example.rmas.repositories

import com.example.rmas.models.Tag
import com.example.rmas.models.UserTag

class TagRepository {
    fun getAllTags(): List<Tag> {
        return listOf(
            Tag("0", "Waterfall"),
            Tag("1", "Mountain"),
            Tag("2", "Lake"),
            Tag("3", "River"),
            Tag("4", "Canyon"),
            Tag("5", "Trail"),
            Tag("5", "Dessert"),
            Tag("5", "Meadow")
        )
    }
}