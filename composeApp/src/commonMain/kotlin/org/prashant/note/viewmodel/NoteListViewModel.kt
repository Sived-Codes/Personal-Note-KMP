package org.prashant.note.viewmodel

import androidx.compose.runtime.Immutable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.prashant.note.core.AppGraph
import org.prashant.note.core.BaseViewModel
import org.prashant.note.model.Note
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


@Immutable
data class NoteListUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val notes: List<NoteItemUi> = emptyList()
)

@Immutable
data class NoteItemUi(
    val id: Long,
    val title: String,
    val date: String
)

class NoteListViewModel : BaseViewModel() {

    private val _uiState = MutableStateFlow(NoteListUiState(isLoading = true))
    val uiState: StateFlow<NoteListUiState> = _uiState

    init {
        loadNotes()
    }

    @OptIn(ExperimentalTime::class)
    private fun todayIso(): String {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        return today.toString()
    }

    private fun Note.toUi() = NoteItemUi(
        id = id,
        title = title,
        date = createdDateIso
    )

    fun onDeleteClicked(id: Long) {
        viewModelScope.launch {
            try {
                AppGraph.deleteNoteUseCase(id)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Delete failed") }
            }
        }
    }

    fun loadNotes() {
        viewModelScope.launch {
            AppGraph.getNotesUseCase()
                .onStart { _uiState.update { it.copy(isLoading = true, error = null) } }
                .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message ?: "Load error") } }
                .collect { list ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = null,
                            notes = list.map { n -> n.toUi() }
                        )
                    }
                    if (list.isEmpty()) {
                        val today = todayIso()
                        AppGraph.createNoteUseCase(
                            Note(
                                title = "Welcome to KMP Notes",
                                bodyHtml = """
                                <h2>Welcome to KMP Notes</h2>
                                <p>This is a <b>sample note</b> with HTML and interactive elements.</p>
                                <button onclick="showInfo('Clicked on Button 1')">Click Me 1</button>
                                <a href="#" onclick="showInfo('Link Clicked')">Click This Link</a>
                                <script>
                                function showInfo(msg) {
                                  if (window.JavaScriptBridge) {
                                    window.JavaScriptBridge.postMessage(msg);
                                  }
                                }
                                </script>
                                """.trimIndent(),
                                createdDateIso = today))
                    }
                }
        }
    }
}
