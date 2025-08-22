package org.prashant.note.domain


import org.prashant.note.data.NotesRepository
import org.prashant.note.model.Note

class GetNoteUseCase(private val repo: NotesRepository) {
    suspend operator fun invoke(id: Long): Note? = repo.getById(id)
}
