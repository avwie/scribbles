import nl.avwie.dom.xml
import nl.avwie.vdom.toNode

fun demo(text: String) = xml("div") {
    "p" {
        text("This is your personalized greeting: ")
    }
    "p" {
        text(text)
    }
}

fun main() {
    val node = demo("Hello world").toNode()

}