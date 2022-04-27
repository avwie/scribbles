package nl.avwie.vdom

import kotlin.test.Test

class RendererTests {

    fun node(color: String) = svg<String> {
        "g" {
            "line" {
                "x1" by 0
                "x2" by 100
                "y1" by 10
                "y2" by 10
            }
        }

        "g" {
            "rect" {
                "x" by 0
                "y" by 0
                "width" by 150
                "height" by 100
                "rx" by 15
                "fill" by "#000000"
            }

            "rect" {
                "x" by 0
                "y" by 0
                "width" by 150
                "height" by 100
                "rx" by 15
                "fill" by color
            }
        }
    }

    @Test
    fun basicTest() {
        val target = TestRendererTarget()
        val renderer = Renderer(target, TestDispatcher())

        renderer.render(node("#ff0000"))
        renderer.render(node("#00ff00"))
    }
}