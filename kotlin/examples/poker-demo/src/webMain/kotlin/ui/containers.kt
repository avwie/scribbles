package ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import org.jetbrains.compose.web.attributes.*
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLInputElement

@Composable fun FullPageCenter(contentBuilder: ContentBuilder<HTMLDivElement>) {
    Div(attrs = {
        classes("d-flex", "justify-content-center", "align-items-center", "vw-100", "vh-100")
    }) {
        contentBuilder()
    }
}

@Composable fun Roompage() {

}