package nl.avwie.vdom

import kotlinx.browser.document

actual object EnvironmentMouseEventData {

    private var clientX: Int = 0
    private var clientY: Int = 0

    init {
        document.addEventListener("mousemove", { event ->
            (event as org.w3c.dom.events.MouseEvent).let { mouseEvent ->
                clientX = mouseEvent.clientX
                clientY = mouseEvent.clientY
            }
        })
    }

    actual fun x(): Int = clientX
    actual fun y(): Int = clientY
}