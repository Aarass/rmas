package com.example.rmas.models

import com.google.firebase.firestore.DocumentId

data class Tag (
    @DocumentId
    val id: String,
    val name: String,
)

data class UserTag(val tag: Tag, var selected: Boolean = true)