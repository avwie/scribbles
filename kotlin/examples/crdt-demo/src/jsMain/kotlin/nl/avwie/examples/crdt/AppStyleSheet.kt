package nl.avwie.examples.crdt

import nl.avwie.examples.crdt.AppStyleSheet.Colors.blue
import nl.avwie.examples.crdt.AppStyleSheet.Colors.darkGrey
import nl.avwie.examples.crdt.AppStyleSheet.Colors.grey
import nl.avwie.examples.crdt.AppStyleSheet.Colors.transparent
import nl.avwie.examples.crdt.AppStyleSheet.Colors.white
import org.jetbrains.compose.web.css.*

object AppStyleSheet : StyleSheet() {

    object Colors {
        val grey = rgb(240, 240, 240)
        val darkGrey = rgb(220, 220, 220)
        val blue = rgb(52, 128, 235)
        val transparent = rgba(255, 255, 255, 255)
        val white = rgb(240, 240, 240)
    }

    init {
        "body *" style {
            fontFamily("'Mali'", "cursive")
        }

        "body" style {
            fontSize(14.px)
        }
    }

    val fullPageCentered by style {
        height(100.vh)
        width(100.vw)
        display(DisplayStyle.Flex)
        alignItems(AlignItems.Center)
        justifyContent(JustifyContent.Center)
    }

    val title by style {
        textAlign("center")
    }

    val nameInput by style {
        fontWeight(600)
    }

    val mainPanel by style {
        minWidth(500.px)
        borderRadius(0.5.em)
        property("box-shadow", "0 .5rem 1rem rgba(0,0,0,.15)")
    }

    val rowContainer by style {
        display(DisplayStyle.Flex)
        flexDirection(FlexDirection.Column)
    }

    val colContainer by style {
        display(DisplayStyle.Flex)
        flexDirection(FlexDirection.Row)
    }

    val responsiveInput by style {
        borderWidth(0.px, 0.px, 2.px, 0.px)
        property("border-color", transparent.toString())
        padding(1.0.em)
        fontSize(1.0.cssRem)

        self + hover style {
            cursor("text")
            backgroundColor(grey)
            property("border-color", darkGrey.toString())
        }

        self + focus style {
            outlineWidth("0")
            property("border-color", blue.toString())
        }

        property("transition-property", "all")
        property("transition-duration", "0.5s")
    }

    val separator by style {
        boxSizing("border-box")
        overflow("visible")
        height(2.px)
        property("margin", "2px 0px")
        property("border-width", "0")
        property("background-color", darkGrey.toString())
    }

    val item by style {
        padding(0.5.em)
        margin(0.5.em)

        ".$colContainer" style {
            alignItems(AlignItems.Center)
        }

        "span" style {
            cursor("pointer")
        }

        "p" style {
            flexGrow(1)
            property("margin", "0 0 0 0.5em")
            property("margin-block-start", "0")
            property("margin-block-end", "0")
        }
    }

    val finished by style {
        textDecoration("line-through")
    }

    val radiusTop by style {
        borderRadius(0.5.em, 0.5.em, 0.em, 0.em)
    }

    val radiusBottom by style {
        borderRadius(0.em, 0.em, 0.5.em, 0.em)
    }
}