package ui

import androidx.compose.runtime.Composable

/*import LocalHistory
import androidx.compose.runtime.Composable
import common.routing.Location
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.ContentBuilder
import org.w3c.dom.HTMLAnchorElement
import router.Route

@Composable fun Link(
    href: String? = null,
    route: Route? = null,
    attrs: AttrBuilderContext<HTMLAnchorElement>? = null,
    content: ContentBuilder<HTMLAnchorElement>? = null
) {
    val history =  LocalHistory.current
    A(href = route?.href ?: href, attrs = {
        attrs?.invoke(this)
        onClick { event ->
            event.preventDefault()
            history.push(Location.parse((event.target as HTMLAnchorElement).href))
        }
    }) {
        content?.invoke(this)
    }
}*/