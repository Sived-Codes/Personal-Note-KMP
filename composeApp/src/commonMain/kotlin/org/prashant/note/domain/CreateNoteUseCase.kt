package org.prashant.note.domain

import org.prashant.note.data.NotesRepository
import org.prashant.note.model.Note

class CreateNoteUseCase(private val repo: NotesRepository) {
    suspend operator fun invoke(note: Note): Long = repo.create(note)
}
