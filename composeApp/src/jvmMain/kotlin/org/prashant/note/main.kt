package org.prashant.note

import androidx.compose.ui.window.application
import androidx.compose.ui.window.Window

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "KMP Notes") {
        App()
    }
}
