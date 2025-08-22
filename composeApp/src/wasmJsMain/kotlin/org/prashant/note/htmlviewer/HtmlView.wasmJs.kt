package org.prashant.note.htmlviewer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import kotlinx.browser.document
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLIFrameElement
import org.w3c.dom.events.Event

@Composable
actual fun HtmlView(html: String, onMessage: (String) -> Unit) {
    DisposableEffect(html) {
        val root = (document.getElementById("root") as HTMLElement).also {
            if (it.style.position.isNullOrEmpty()) it.style.position = "relative"
        }

        // Create the iframe that will show the HTML
        val iframe = document.createElement("iframe") as HTMLIFrameElement
        iframe.srcdoc = html
        iframe.style.position = "absolute"
        iframe.style.top = "0"
        iframe.style.left = "0"
        iframe.style.right = "0"
        iframe.style.bottom = "0"
        iframe.style.width = "100%"
        iframe.style.height = "100%"
        iframe.style.border = "0"
        iframe.style.background = "transparent"

        root.appendChild(iframe)

        val clickHandler: (Event) -> Unit = { e ->
            val target = e.target
            if (target is HTMLAnchorElement) {
                e.preventDefault()
                onMessage("Link clicked: ${target.href}")
            }
        }

        val loadHandler: (Event) -> Unit = {
            iframe.contentDocument?.addEventListener("click", clickHandler)
        }

        iframe.addEventListener("load", loadHandler)

        onDispose {
            iframe.contentDocument?.removeEventListener("click", clickHandler)
            iframe.removeEventListener("load", loadHandler)
            root.removeChild(iframe)
        }
    }
}

