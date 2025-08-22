package org.prashant.note.viewmodel

import androidx.compose.runtime.Immutable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.prashant.note.core.AppGraph
import org.prashant.note.core.BaseViewModel
import org.prashant.note.model.Note
import kotlin.time.ExperimentalTime
import kotlin.time.Clock
@Immutable
data class NoteEditUiState(
    val title: String = "",
    val bodyHtml: String = "",
    val createdDateIso: String = defaultToday(),
    val isSaving: Boolean = false,
    val error: String? = null,
    val saved: Boolean = false
)

@OptIn(ExperimentalTime::class)
private fun defaultToday(): String =
    Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()

class NoteEditViewModel : BaseViewModel() {

    private val _ui = MutableStateFlow(NoteEditUiState())
    val ui: StateFlow<NoteEditUiState> = _ui

    fun onTitleChange(v: String) = _ui.update { it.copy(title = v) }
    fun onBodyChange(v: String) = _ui.update { it.copy(bodyHtml = v) }
    fun onDateChange(v: String) = _ui.update { it.copy(createdDateIso = v) }

    fun save() {
        val s = _ui.value
        if (s.title.isBlank()) {
            _ui.update { it.copy(error = "Title required") }
            return
        }
        if (s.createdDateIso.isBlank()) {
            _ui.update { it.copy(error = "Date required") }
            return
        }
        viewModelScope.launch {
            try {
                _ui.update { it.copy(isSaving = true, error = null) }
                AppGraph.createNoteUseCase(
                    Note(
                        title = s.title.trim(),
                        bodyHtml = s.bodyHtml,
                        createdDateIso = s.createdDateIso
                    )
                )
                _ui.update { it.copy(isSaving = false, saved = true) }
            } catch (e: Exception) {
                _ui.update { it.copy(isSaving = false, error = e.message ?: "Save failed") }
            }
        }
    }
}
