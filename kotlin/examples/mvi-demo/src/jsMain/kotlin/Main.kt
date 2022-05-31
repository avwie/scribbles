import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import common.mvi.invoke
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.utils.io.core.*
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.renderComposable
import kotlin.random.Random

actual suspend fun makeXKCDRequest(id: String): Comic = HttpClient().use { client ->
    val response: String = client.get("https://xkcd.vercel.app/?comic=$id")
    val json = JSON.parse<dynamic>(response)
    Comic(id = json.num as Int, title = json.title as String, img = json.img as String, alt = json.alt as String)
}

fun main() {
    renderComposable("root") {
        val state by store.state.collectAsState()
        val dispatch = rememberDispatcher()

        LaunchedEffect(Unit) {
            dispatch(Effect.LoadLatestComic)
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

            Button(attrs = {
                if (comic.id == state.comicCount) disabled()
                onClick { dispatch(Effect.LoadComic(state.comicCount!!))}
            }) {
                Text("Latest")
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