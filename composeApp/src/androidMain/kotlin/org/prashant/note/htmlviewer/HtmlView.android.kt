package org.prashant.note.htmlviewer

import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView

private class JsBridge(private val onMessage: (String) -> Unit) {
    @JavascriptInterface
    fun postMessage(msg: String) = onMessage(msg)
}

@Composable
actual fun HtmlView(html: String, onMessage: (String) -> Unit) {
    AndroidView(factory = { ctx ->
        WebView(ctx).apply {
            settings.javaScriptEnabled = true
            addJavascriptInterface(JsBridge(onMessage), "JavaScriptBridge")
            webViewClient = object : WebViewClient() {}
            loadDataWithBaseURL(null, html, "text/html", "utf-8", null)
        }
    })
}
