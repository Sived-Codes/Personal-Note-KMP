package org.prashant.note

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.prashant.note.htmlviewer.HtmlViewerScreen
import org.prashant.note.view.NoteDetailScreen
import org.prashant.note.view.NoteEditScreen
import org.prashant.note.view.NoteListScreen
import org.prashant.note.view.PdfScreen


sealed interface Screen {
    data object NotesList : Screen
    data object NoteEdit : Screen
    data object HtmlViewer : Screen
    data class NoteDetail(val noteId: Long) : Screen
    data object Pdf : Screen
}

private const val PDF_URL = "https://qa.pilloo.ai/GeneratedPDF/Companies/202/2025-2026/DL.pdf"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val sampleHtml = """
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
""".trimIndent()

    MaterialTheme {
        var currentScreen by remember { mutableStateOf<Screen>(Screen.NotesList) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            when (currentScreen) {
                                Screen.NotesList -> "KMP Notes"
                                Screen.NoteEdit -> "Create Note"
                                Screen.HtmlViewer -> "HTML Viewer"
                                is Screen.NoteDetail -> "Note Details"
                                Screen.Pdf -> "PDF Viewer"
                            }
                        )
                    },
                    navigationIcon = {
                        if (currentScreen != Screen.NotesList) {
                            IconButton(onClick = { currentScreen = Screen.NotesList }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        }
                    }
                )
            }
        ) { padding ->
            Box(Modifier.padding(padding)) {
                when (val screen = currentScreen) {
                    Screen.NotesList -> NoteListScreen(
                        onAddClicked = { currentScreen = Screen.NoteEdit },
                        onOpenHtml = { currentScreen = Screen.HtmlViewer },
                        onOpenPdf = { currentScreen = Screen.Pdf },
                        onOpenNote = { noteId -> currentScreen = Screen.NoteDetail(noteId) }
                    )

                    Screen.NoteEdit -> NoteEditScreen(
                        onDone = { currentScreen = Screen.NotesList }
                    )

                    is Screen.NoteDetail -> NoteDetailScreen(
                        noteId = screen.noteId,
                        onBack = { currentScreen = Screen.NotesList }
                    )

                    Screen.HtmlViewer -> HtmlViewerScreen(
                        html = sampleHtml,
                        onClose = { currentScreen = Screen.NotesList }
                    )

                    Screen.Pdf -> PdfScreen(
                        url = PDF_URL,
                        onClose = { currentScreen = Screen.NotesList }
                    )
                }
            }
        }
    }
}
