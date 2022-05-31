import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.renderComposable
import kotlin.random.Random


fun main() {
    renderComposable("root") {
        val state by store.state.collectAsState()
        val dispatch = rememberDispatcher()

        LaunchedEffect(Unit) {
            store.dispatchEffect(Effect.LoadLatestComic)
        }

        if (state.currentComic == null) {
            P {
                Text("Loading ....")
            }
        } else {
            val comic = state.currentComic!!
            H1 {
                Text(comic.title)
            }

            Img(src = comic.img)

            P {
                Text(comic.alt)
            }

            Button(attrs = {
                if (comic.id <= 1) disabled()
                onClick { dispatch(Effect.LoadComic(comic.id - 1)) }
            }) {
                Text("Previous")
            }

            Button(attrs = {
                onClick { dispatch(Effect.LoadComic(Random.nextInt(1, state.comicCount!!)))}
            }) {
                Text("Random")
            }

            Button (attrs = {
                if (comic.id == state.comicCount) disabled()
                onClick { dispatch(Effect.LoadComic(comic.id + 1)) }
            }) {
                Text("Next")
            }
        }
    }
}