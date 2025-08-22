package org.prashant.note.domain


import kotlinx.coroutines.flow.Flow
import org.prashant.note.data.NotesRepository
import org.prashant.note.model.Note

class GetNotesUseCase(private val repo: NotesRepository) {
    operator fun invoke(): Flow<List<Note>> = repo.observeAll()
}
