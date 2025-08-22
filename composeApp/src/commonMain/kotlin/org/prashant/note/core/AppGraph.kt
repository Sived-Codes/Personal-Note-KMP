package org.prashant.note.core


import org.prashant.note.data.InMemoryNotesRepository
import org.prashant.note.data.NotesRepository
import org.prashant.note.domain.CreateNoteUseCase
import org.prashant.note.domain.DeleteNoteUseCase
import org.prashant.note.domain.GetNoteUseCase
import org.prashant.note.domain.GetNotesUseCase
import org.prashant.note.domain.UpdateNoteUseCase

object AppGraph {
    private val notesRepository: NotesRepository by lazy { InMemoryNotesRepository() }
    val getNotesUseCase by lazy { GetNotesUseCase(notesRepository) }
    val createNoteUseCase by lazy { CreateNoteUseCase(notesRepository) }
    val deleteNoteUseCase by lazy { DeleteNoteUseCase(notesRepository) }
    val getNoteUseCase by lazy { GetNoteUseCase(notesRepository) }
    val updateNoteUseCase by lazy { UpdateNoteUseCase(notesRepository) }
}
