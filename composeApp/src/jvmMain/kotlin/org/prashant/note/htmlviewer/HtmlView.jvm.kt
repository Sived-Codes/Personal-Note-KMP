package org.prashant.note.htmlviewer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import java.awt.BorderLayout
import javax.swing.JEditorPane
import javax.swing.JPanel
import javax.swing.JScrollPane

@Composable
actual fun HtmlView(html: String, onMessage: (String) -> Unit) {
    SwingPanel(
        modifier = Modifier.fillMaxSize(),
        factory = {
            val editorPane = JEditorPane("text/html", html).apply {
                isEditable = false
            }
            val scrollPane = JScrollPane(editorPane)
            val panel = JPanel(BorderLayout())
            panel.add(scrollPane, BorderLayout.CENTER)
            panel
        }
    )
}
