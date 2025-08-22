package org.prashant.note.htmlviewer


import androidx.compose.runtime.*
import androidx.compose.material3.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.prashant.note.core.BaseViewModel

data class HtmlViewerUiState(
    val html: String,
    val lastMessage: String? = null
)

class HtmlViewerViewModel(html: String) : BaseViewModel() {
    private val _ui = MutableStateFlow(HtmlViewerUiState(html))
    val ui: StateFlow<HtmlViewerUiState> = _ui

    fun onHtmlMessage(msg: String) {
        _ui.update { it.copy(lastMessage = msg) }
    }

    fun clearMessage() {
        _ui.update { it.copy(lastMessage = null) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HtmlViewerScreen(
    html: String,
    onClose: () -> Unit,
    vm: HtmlViewerViewModel = remember { HtmlViewerViewModel(html) }
) {
    val state by vm.ui.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    var dialog by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(state.lastMessage) {
        state.lastMessage?.let {
            snackbar.showSnackbar(it)
            dialog = it
            vm.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        HtmlView(
            html = state.html,
            onMessage = vm::onHtmlMessage
        )
    }

    dialog?.let { msg ->
        AlertDialog(
            onDismissRequest = { dialog = null },
            confirmButton = { TextButton(onClick = { dialog = null }) { Text("OK") } },
            title = { Text("Message") },
            text = { Text(msg) }
        )
    }
}
