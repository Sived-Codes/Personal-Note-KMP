package org.prashant.note.data


import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.prashant.note.model.Note

class InMemoryNotesRepository : NotesRepository {

    private val items = mutableListOf<Note>()
    private val autoId = IdGen()
    private val state = MutableStateFlow<List<Note>>(emptyList())

    override fun observeAll(): Flow<List<Note>> = state.asStateFlow()

    override suspend fun getById(id: Long): Note? = items.firstOrNull { note -> note.id == id }

    override suspend fun create(note: Note): Long {
        val id = autoId.next()
        val withId = note.copy(id = id)
        items.add(withId)
        state.value = ArrayList(items)
        return id
    }

    override suspend fun update(note: Note) {
        val idx = items.indexOfFirst { item -> item.id == note.id }
        if (idx == -1) throw IllegalArgumentException("Note not found id=${note.id}")
        items[idx] = note
        state.value = ArrayList(items)
    }

    override suspend fun delete(id: Long) {
        val removed = items.removeAll { item -> item.id == id }
        if (!removed) throw IllegalArgumentException("Note not found id=$id")
        state.value = ArrayList(items)
    }

    override suspend fun clear() {
        items.clear()
        state.value = emptyList()
    }

    private class IdGen {
        private var current = 0L
        fun next(): Long = ++current
    }
}
