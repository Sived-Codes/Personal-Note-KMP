package org.prashant.note.data


import kotlinx.coroutines.flow.Flow
import org.prashant.note.model.Note

interface NotesRepository {
    fun observeAll(): Flow<List<Note>>
    suspend fun getById(id: Long): Note?
    suspend fun create(note: Note): Long
    suspend fun update(note: Note)
    suspend fun delete(id: Long)
    suspend fun clear()
}
