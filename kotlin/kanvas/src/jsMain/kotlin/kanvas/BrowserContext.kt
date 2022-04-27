package kanvas

import kotlinx.browser.document

class BrowserContext : Context {

    override var mouseX: Int = 0
        private set

    override var mouseY: Int = 0
        private set

    init {
        document.addEventListener("mousemove", { event ->
            (event as org.w3c.dom.events.MouseEvent).let { mouseEvent ->
                mouseX = mouseEvent.clientX
                mouseY = mouseEvent.clientY
            }
        })
    }
}