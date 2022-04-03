import kotlinx.browser.document
import nl.avwie.dom.svg
import nl.avwie.dom.toElement

fun main() {
    val app = document.getElementById("app")!!
    val test = svg(width = 300, height = 200) {
        "rect" ("width" to "100%", "height" to "100%", "fill" to "red")
        "circle" ("cx" to 150, "cy" to 100, "r" to 80, "fill" to "green")
        "text" ("x" to 150, "y" to 125, "font-size" to 60, "text-anchor" to "middle", "fill" to "white") {
            text("SVG")
        }
    }

    repeat(10) {
        app.appendChild(test.toElement())
    }
}