package org.prashant.note.model

data class Note(
    val id: Long = 0L,
    val title: String,
    val bodyHtml: String,
    val createdDateIso: String
)
