package org.prashant.note.domain


import org.prashant.note.data.NotesRepository

class DeleteNoteUseCase(private val repo: NotesRepository) {
    suspend operator fun invoke(id: Long) = repo.delete(id)
}
