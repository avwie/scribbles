package nl.avwie.vdom

object Fixtures {
    fun basicHTML(message: String, headerSize: Int = 1) = html {
        "head" {
            "title" {
                + "Demo"
            }
        }

        "body" {
            "h$headerSize" {
                "id" by "message"
                + message
            }
        }
    }

    fun basicSVG(message: String, radius: Int = 80) = svg {
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
            "r" by "$radius"
            "fill" by "green"
        }

        "text" {
            "x" by "150"
            "y" by "125"
            "font-size" by "60"
            "text-anchor" by "middle"
            "fill" by "white"
            + message
        }
    }
}