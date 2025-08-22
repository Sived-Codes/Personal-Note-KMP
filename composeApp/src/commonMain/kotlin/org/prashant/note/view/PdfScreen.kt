package org.prashant.note.view


import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import org.prashant.note.pdfviewer.PdfViewer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfScreen(
    url: String,
    onClose: () -> Unit
) {
    Scaffold() { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            shadowElevation = 4.dp
        ) {
            PdfViewer(url)
        }
    }
}
