package nl.avwie.vdom

import kotlinx.browser.document
import kotlinx.browser.window

fun demo(rotation: Int) = svg {
    "width" by "300"
    "height" by "200"

    "rect" {
        "width" by "100%"
        "height" by "100%"
        "fill" by "red"
    }

    "circle" {
        "cx" by "150"
        "cy" by "100"
        "r" by "80"
        "fill" by "green"
    }

    "text" {
        "transform" by "rotate($rotation, 150, 100)"
        "x" by "150"
        "y" by "125"
        "font-size" by "60"
        "text-anchor" by "middle"
        "fill" by "white"
        + "SVG"
    }
}

fun main() {
    val container = document.getElementById("container")!!
    val target = BrowserDocumentTarget(container)
    val renderer = Renderer(target)

    var angle = 0
    window.setInterval({
        renderer.render(demo(angle))
        angle += 1
    }, 1000 / 60)
}