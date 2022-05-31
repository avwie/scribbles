import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.rememberCoroutineScope
import common.mvi.*
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

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

expect suspend fun makeXKCDRequest(id: String): Comic

val store = Store(State(), actionReducer, effectHandler)

val LocalSuspendingDispatcher = compositionLocalOf<SuspendingDispatcher<Action, Effect>> { store }

@Composable
inline fun rememberDispatcher(
    getContext: @DisallowComposableCalls () -> CoroutineContext = { Dispatchers.Main }
): ScopedDispatcher<Action, Effect> {
    val scope = rememberCoroutineScope(getContext)
    val dispatcher = LocalSuspendingDispatcher.current
    return ScopedDispatcher(dispatcher, scope.coroutineContext)
}