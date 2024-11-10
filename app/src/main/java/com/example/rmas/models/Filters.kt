package com.example.rmas.models

data class Filters(
    val authorId: String? = null,
    val dateRange: Pair<Long?, Long?> = Pair(null, null),
    val locationRange: Float? = null,
    val activeTags: List<String> = emptyList(),
)
