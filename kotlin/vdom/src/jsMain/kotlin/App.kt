import kotlinx.browser.document
import kotlinx.browser.window
import nl.avwie.dom.html
import nl.avwie.vdom.BrowserRenderTarget
import nl.avwie.vdom.Renderer
import nl.avwie.vdom.toNode

fun demo(text: String) = html("div") {
    "p" {
        text("This is your personalized greeting: ")
    }
    "p" {
        "b" {
            text(text)
        }
    }

    "p" {
        text("Something below!!!")
    }
}

fun main() {
    var i = 0
    val node = demo("Timer is ${++i}").toNode()
    val renderTarget = BrowserRenderTarget(document.body!!)
    val renderer = Renderer(renderTarget)
    renderer.render(node)

    renderer.render(demo("Timer is 100").toNode())

    /*window.setInterval({
       renderer.render(demo("Timer is ${++i}").toNode())
    }, 1000)*/
}