package ui

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.*

@Composable
fun ErrorPage(title: String, message: String) {
    Div(attrs = { classes("text-center")}) {
        H2(attrs = { classes("text-danger") }) {
            Text(title)
        }
        P {
            Text(message)
        }
    }
}