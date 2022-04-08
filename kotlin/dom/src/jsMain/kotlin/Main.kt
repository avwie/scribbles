import externals.virtualDom.diff
import kotlinx.browser.document
import kotlinx.browser.window
import nl.avwie.dom.*

fun demo(angle: Double): Definition = svg(width = 300, height = 200) {
    "rect" ("width" to "100%", "height" to "100%", "fill" to "red")
    "g" ("transform" to "rotate($angle, 150, 100)") {
        "circle"("cx" to 150, "cy" to 100, "r" to 80, "fill" to "green")
        "text"("x" to 150, "y" to 125, "font-size" to 60, "text-anchor" to "middle", "fill" to "white") {
            text("SVG")
        }
    }
}

fun main() {
    val app = document.getElementById("app")!!

    // render them as normal elements
    repeat(4) { i ->
        app.appendChild(demo(i * 90.0).toElement())
    }

    // render vdom
    var currentAngle = 0.0
    var tree = demo(currentAngle).toVNode()
    var element = tree.toElement()
    app.appendChild(element)

    window.setInterval({
        currentAngle += 0.6
        currentAngle %= 360.0
        val newTree = demo(currentAngle).toVNode()
        val patches = newTree.diff(tree)
        element = element.patch(patches)
        tree = newTree
    }, 1000 / 60)
}