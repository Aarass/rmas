package com.example.rmas.models

data class Filters(
    val author: User? = null,
    val dateRange: Pair<Long?, Long?> = Pair(null, null),
    val locationRange: Float? = null,
    val activeTags: List<String> = emptyList(),
)
