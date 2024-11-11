package com.example.rmas.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.getField

data class Tag (
    @DocumentId
    val id: String,
    val name: String,
) {
    companion object {
        fun from (document: DocumentSnapshot): Tag {
            return Tag(
                id = document.id,
                name = document.getField<String>("name") ?: "#err#"
            )
        }
    }
}

data class UserTag(val tag: Tag, var selected: Boolean = true)