package org.prashant.note.pdfviewer

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.PDFRenderer
import java.awt.image.BufferedImage
import java.io.File
import javax.swing.ImageIcon
import javax.swing.JLabel

@Composable
actual fun PdfViewer(url: String) {
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var pages by remember { mutableStateOf<List<BufferedImage>>(emptyList()) }
    var currentPage by remember { mutableStateOf(0) }
    var scale by remember { mutableStateOf(1.0f) }

    LaunchedEffect(url) {
        try {
            loading = true
            error = null
            val file = withContext(Dispatchers.IO) { downloadPdf(url) }
            val rendered = withContext(Dispatchers.IO) { renderPdfPages(file, scale) }
            pages = rendered
            loading = false
        } catch (e: Exception) {
            error = e.message
            loading = false
        }
    }

    Box(Modifier.fillMaxSize()) {
        when {
            loading -> LinearProgressIndicator(
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
            )

            error != null -> Column(
                Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Error: $error", color = MaterialTheme.colorScheme.error)
                Button(onClick = {
                    error = null
                    loading = true
                }) { Text("Retry") }
            }

            pages.isNotEmpty() -> Column {
                Row(
                    Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { if (currentPage > 0) currentPage-- },
                        enabled = currentPage > 0
                    ) { Text("Previous") }

                    Text("Page ${currentPage + 1} / ${pages.size}")

                    Button(
                        onClick = { if (currentPage < pages.size - 1) currentPage++ },
                        enabled = currentPage < pages.size - 1
                    ) { Text("Next") }

                    Button(onClick = {
                        scale += 0.2f
                        // Trigger re-render with new scale
                        loading = true
                    }) { Text("Zoom +") }

                    Button(onClick = {
                        if (scale > 0.4f) {
                            scale -= 0.2f
                            loading = true
                        }
                    }) { Text("Zoom -") }
                }

                SwingPanel(
                    factory = { JLabel(ImageIcon(pages[currentPage])) },
                    modifier = Modifier.fillMaxSize(),
                    update = { it.icon = ImageIcon(pages[currentPage]) }
                )
            }
        }
    }
}

private fun downloadPdf(url: String): File {
    val client = OkHttpClient()
    val request = Request.Builder().url(url).build()
    client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) error("HTTP ${response.code}")

        val cacheDir = File(System.getProperty("java.io.tmpdir"), "kmp-notes-pdf")
        cacheDir.mkdirs()
        val outFile = File(cacheDir, "document.pdf")

        response.body?.byteStream()?.use { input ->
            outFile.outputStream().use { output ->
                input.copyTo(output)
            }
        } ?: error("Empty response body")

        return outFile
    }
}

private fun renderPdfPages(file: File, scale: Float): List<BufferedImage> {
    val document = PDDocument.load(file)
    val renderer = PDFRenderer(document)
    val pages = mutableListOf<BufferedImage>()

    for (i in 0 until document.numberOfPages) {
        pages.add(renderer.renderImageWithDPI(i, 72f * scale))
    }

    document.close()
    return pages
}
