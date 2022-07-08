import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.jetbrains.compose.web.css.minWidth
import org.jetbrains.compose.web.dom.DOMScope
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.Element

@Composable
fun Centered(content: @Composable DOMScope<Element>.() -> Unit) {
    Div(attrs = {
        classes("vw-100", "vh-100", "d-flex", "align-items-center", "justify-content-center")
    }) {
        Div(attrs = {
            classes("shadow", "rounded")
            style {
                minWidth("500px")
            }
        }) {
            content()
        }
    }
}

@Composable
fun ResponsiveContainer(content: @Composable DOMScope<Element>.() -> Unit) {
    Div(attrs = {
        classes("container-sm")
    }) {
        content()
    }
}

@Composable
fun Row(content: @Composable DOMScope<Element>.() -> Unit) {
    Div(attrs = { classes("row") }) { content() }
}

@Composable
fun Col(size: Int, type: String? = null, content: @Composable DOMScope<Element>.() -> Unit) {
    val className = remember {
        when (type) {
            null -> "col-$size"
            else -> "col-$type-$size"
        }
    }

    Div(attrs = { classes(className)}) {
        content()
    }
}