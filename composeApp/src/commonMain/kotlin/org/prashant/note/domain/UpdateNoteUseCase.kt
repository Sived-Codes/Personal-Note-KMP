package org.prashant.note.domain


import org.prashant.note.data.NotesRepository
import org.prashant.note.model.Note

class UpdateNoteUseCase(private val repo: NotesRepository) {
    suspend operator fun invoke(note: Note) = repo.update(note)
}
