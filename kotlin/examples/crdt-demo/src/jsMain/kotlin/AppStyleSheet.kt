import AppStyleSheet.Colors.blue
import AppStyleSheet.Colors.darkGrey
import AppStyleSheet.Colors.grey
import AppStyleSheet.Colors.transparent
import org.jetbrains.compose.web.css.*

object AppStyleSheet : StyleSheet() {

    object Colors {
        val grey = rgb(240, 240, 240)
        val darkGrey = rgb(220, 220, 220)
        val blue = rgb(52, 128, 235)
        val transparent = rgba(255, 255, 255, 255)
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

    val mainPanel by style {
        minWidth(500.px)
        borderRadius(0.5.em)
        property("box-shadow", "0 .5rem 1rem rgba(0,0,0,.15)")
    }

    val rowContainer by style {
        display(DisplayStyle.Flex)
        flexDirection(FlexDirection.Column)
    }

    val responsiveInput by style {
        borderWidth(0.px, 0.px, 2.px, 0.px)
        property("border-color", transparent.toString())
        borderRadius(0.5.em)
        padding(0.5.em)
        margin(0.5.em)
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
}