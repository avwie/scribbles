package nl.avwie.vdom

import kotlinx.browser.document
import kotlinx.browser.window
import kotlin.random.Random

fun tree(message: String) = html("div") {
    "h1" {
        + "Welcome to this demo"
    }

    "div" {
        (if (Random.nextBoolean()) "h2" else "h3") {
            + "The user input is described below:"
        }

        "h3" {
            (if (Random.nextBoolean()) "b" else "i") {
                + message
            }
        }

        "p" {
            + "This is a footer"
        }
    }
}

fun main() {
    val container = document.getElementById("container")!!
    val target = BrowserRendererTarget(container)
    val renderer = Renderer(target)

    var counter = 0
    window.setInterval({
        renderer.render(tree("Counter is: ${++counter}"))
    }, 1000)
}