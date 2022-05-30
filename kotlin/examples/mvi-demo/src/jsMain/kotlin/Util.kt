import androidx.compose.runtime.*
import common.mvi.Dispatcher
import common.mvi.ScopedDispatcher
import common.mvi.Store
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

val LocalDispatcher = compositionLocalOf<Dispatcher<Action, Effect>> {
    error("Dispatcher not set")
}

@Composable inline fun rememberDispatcher(
    getContext: @DisallowComposableCalls () -> CoroutineContext = { Dispatchers.Main }
): ScopedDispatcher<Action, Effect> {
    val scope = rememberCoroutineScope(getContext)
    val dispatcher = LocalDispatcher.current
    return ScopedDispatcher(dispatcher, scope.coroutineContext)
}