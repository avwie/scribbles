package nl.avwie.vdom

import org.junit.Test
import org.w3c.dom.Element
import kotlin.test.assertEquals

class DOMDocumentTargetTests {

    @Test
    fun simple() {
        val target = DOMDocumentTarget()
        val renderer = Renderer(target)
        renderer.render(Fixtures.basicHTML("Hello world!"))
        assertEquals(renderer.rootElement!!.getElementsByTagName("h1").item(0).textContent, "Hello world!")
    }

    @Test
    fun replaceRootNode() {
        val target = DOMDocumentTarget()
        val renderer = Renderer(target)
        renderer.render(Fixtures.basicHTML("Hello world!"))
        renderer.render(Fixtures.basicSVG("Hello world!"))
        assertEquals(renderer.rootElement!!.tagName, "svg")
    }

    @Test
    fun replaceInnerNode() {
        val target = DOMDocumentTarget()
        val renderer = Renderer(target)
        renderer.render(Fixtures.basicHTML("Hello world!"))
        renderer.render(Fixtures.basicHTML("Hello world smaller!", headerSize = 2))
        assertEquals(renderer.rootElement!!.getElementsByTagName("h2").item(0).textContent, "Hello world smaller!")
    }

    @Test
    fun updateAttributeAndText() {
        val target = DOMDocumentTarget()
        val renderer = Renderer(target)
        renderer.render(Fixtures.basicSVG("Hello world!"))
        renderer.render(Fixtures.basicSVG("Hello world 2!", radius = 50))
        assertEquals(renderer.rootElement!!.getElementsByTagName("text").item(0).textContent, "Hello world 2!")
        assertEquals((renderer.rootElement!!.getElementsByTagName("circle").item(0) as Element).getAttribute("r"), "50")
    }
}