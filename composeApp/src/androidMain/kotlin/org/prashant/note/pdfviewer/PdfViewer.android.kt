package org.prashant.note.pdfviewer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import okhttp3.OkHttpClient
import okhttp3.Request

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun PdfViewer(
    url: String,
) {
    Scaffold(
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            PdfRendererContent(url = url)
        }
    }
}

@Composable
private fun PdfRendererContent(url: String) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var pages by remember { mutableStateOf<List<Bitmap>>(emptyList()) }
    LaunchedEffect(url) {
        try {
            isLoading = true
            error = null
            val file = withContext(Dispatchers.IO) { downloadPdfToCache(context, url) }
            val rendered = withContext(Dispatchers.IO) { renderAllPages(file) }
            pages = rendered
        } catch (e: Exception) {
            error = e.message ?: "Failed to load PDF"
        } finally {
            isLoading = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading -> {
                LinearProgressIndicator(
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                )
            }

            error != null -> {
                Text(
                    text = "Error: $error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(12.dp)
                ) {
                    itemsIndexed(pages) { index, bmp ->
                        Image(
                            bitmap = bmp.asImageBitmap(),
                            contentDescription = "Page ${index + 1}",
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun downloadPdfToCache(context: Context, url: String): File {
    val client = OkHttpClient()
    val request = Request.Builder().url(url).build()
    client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) error("HTTP ${response.code}")
        val outFile = File(context.cacheDir, "downloaded.pdf")
        response.body?.byteStream()?.use { input ->
            outFile.outputStream().use { output ->
                input.copyTo(output)
            }
        } ?: error("Empty response body")
        return outFile
    }
}

private fun renderAllPages(file: File): List<Bitmap> {
    val pfd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
    val renderer = PdfRenderer(pfd)
    val list = mutableListOf<Bitmap>()
    for (i in 0 until renderer.pageCount) {
        val page = renderer.openPage(i)
        val width = page.width
        val height = page.height
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        page.close()
        list.add(bmp)
    }
    renderer.close()
    pfd.close()
    return list
}