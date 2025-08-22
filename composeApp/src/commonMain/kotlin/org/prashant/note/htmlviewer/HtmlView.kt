package org.prashant.note.htmlviewer


import androidx.compose.runtime.Composable

@Composable
expect fun HtmlView(html: String, onMessage: (String) -> Unit)
