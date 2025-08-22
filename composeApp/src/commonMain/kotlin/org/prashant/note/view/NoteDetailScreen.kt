package org.prashant.note.view


import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.prashant.note.core.AppGraph
import org.prashant.note.core.BaseViewModel
import org.prashant.note.htmlviewer.HtmlView
import org.prashant.note.model.Note
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight


data class NoteDetailUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val note: Note? = null,
    val lastMessage: String? = null
)

class NoteDetailViewModel(private val noteId: Long) : BaseViewModel() {
    private val _ui = MutableStateFlow(NoteDetailUiState(isLoading = true))
    val ui: StateFlow<NoteDetailUiState> = _ui

    init { load() }

    private fun load() {
        viewModelScope.launch {
            try {
                _ui.update { it.copy(isLoading = true, error = null) }
                val n = AppGraph.getNoteUseCase(noteId)
                if (n == null) _ui.update { it.copy(isLoading = false, error = "Note not found") }
                else _ui.update { it.copy(isLoading = false, note = n) }
            } catch (e: Exception) {
                _ui.update { it.copy(isLoading = false, error = e.message ?: "Load failed") }
            }
        }
    }

    fun onHtmlMessage(msg: String) { _ui.update { it.copy(lastMessage = msg) } }
    fun clearMessage() { _ui.update { it.copy(lastMessage = null) } }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(noteId: Long, onBack: () -> Unit) {
    val vm = remember(noteId) { NoteDetailViewModel(noteId) }
    val state by vm.ui.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    var dialog by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()

    LaunchedEffect(state.lastMessage) {
        state.lastMessage?.let {
            snackbar.showSnackbar(it)
            dialog = it
            vm.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbar) { data ->
                Snackbar(
                    snackbarData = data,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },

    ) { padding ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            strokeWidth = 4.dp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Loading note...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            state.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Error",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                state.error!!,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }

            state.note != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(scrollState)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "Created",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    state.note!!.createdDateIso,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text(
                                "Content",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            HtmlView(
                                html = state.note!!.bodyHtml,
                                onMessage = vm::onHtmlMessage
                            )
                        }
                    }
                }
            }
        }
    }

    dialog?.let { msg ->
        AlertDialog(
            onDismissRequest = { dialog = null },
            confirmButton = {
                TextButton(onClick = { dialog = null }) {
                    Text("OK", fontWeight = FontWeight.Medium)
                }
            },
            title = {
                Text("Message", fontWeight = FontWeight.SemiBold)
            },
            text = { Text(msg) },
            shape = RoundedCornerShape(20.dp)
        )
    }
}

