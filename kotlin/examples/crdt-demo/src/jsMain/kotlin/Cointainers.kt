import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.DOMScope
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.Element

@Composable
fun FullPageCentered(content: @Composable DOMScope<Element>.() -> Unit) {
    Div(attrs = {classes(AppStyleSheet.fullPageCentered) }) {
        Div(attrs = { classes(AppStyleSheet.mainPanel) }) {
            content()
        }
    }
}

@Composable
fun RowContainer(content: @Composable DOMScope<Element>.() -> Unit) {
    Div(attrs = { classes(AppStyleSheet.rowContainer) }) {
        content()
    }
}