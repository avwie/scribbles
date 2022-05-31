import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import common.mvi.ActionReducer
import common.mvi.EffectHandler
import common.mvi.Store
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.renderComposable
import kotlin.random.Random

data class State(
    val comicCount: Int? = null,
    val currentComic: Comic? = null
)

data class Comic(val id: Int, val title: String, val img: String, val alt: String)

sealed interface Action {
    data class SetComicCount(val count: Int) : Action
    data class SetComic(val comic: Comic) : Action
}

sealed interface Effect {
    object LoadLatestComic : Effect
    data class LoadComic(val id: Int) : Effect
}

val actionReducer = ActionReducer<State, Action> { state, action ->
    when (action) {
        is Action.SetComicCount -> state.copy(comicCount = action.count)
        is Action.SetComic -> state.copy(currentComic = action.comic)
    }
}

val effectHandler = EffectHandler<State, Action, Effect> { state, effect, dispatcher ->
    when (effect) {
        Effect.LoadLatestComic -> {
            val latestComic = makeXKCDRequest("latest")
            dispatcher.dispatchAction(Action.SetComicCount(latestComic.id))
            dispatcher.dispatchAction(Action.SetComic(latestComic))
        }

        is Effect.LoadComic -> {
            val comic = makeXKCDRequest(effect.id.toString())
            dispatcher.dispatchAction(Action.SetComic(comic))
        }
    }
}

suspend fun makeXKCDRequest(id: String): Comic = HttpClient().use { client ->
    val response: String = client.get("https://xkcd.vercel.app/?comic=$id")
    val json = JSON.parse<dynamic>(response)
    Comic(id = json.num as Int, title = json.title as String, img = json.img as String, alt = json.alt as String)
}

val store = Store(State(), actionReducer, effectHandler)

fun main() {
    renderComposable("root") {
        val state by store.state.collectAsState()
        val scope = rememberCoroutineScope()

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
                onClick {
                    scope.launch {
                        store.dispatchEffect(Effect.LoadComic(comic.id - 1))
                    }
                }
            }) {
                Text("Previous")
            }

            Button(attrs = {
                onClick {
                    scope.launch {
                        store.dispatchEffect(Effect.LoadComic(Random.nextInt(1, state.comicCount!!)))
                    }
                }
            }) {
                Text("Random")
            }

            Button (attrs = {
                if (comic.id == state.comicCount) disabled()
                onClick {
                    scope.launch {
                        store.dispatchEffect(Effect.LoadComic(comic.id + 1))
                    }
                }
            }) {
                Text("Next")
            }
        }
    }
}