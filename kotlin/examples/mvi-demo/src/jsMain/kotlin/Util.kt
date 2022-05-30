import androidx.compose.runtime.*
import common.mvi.SuspendingDispatcher
import common.mvi.ScopedDispatcher
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

val LocalSuspendingDispatcher = compositionLocalOf<SuspendingDispatcher<Action, Effect>> {
    error("Dispatcher not set")
}

@Composable inline fun rememberDispatcher(
    getContext: @DisallowComposableCalls () -> CoroutineContext = { Dispatchers.Main }
): ScopedDispatcher<Action, Effect> {
    val scope = rememberCoroutineScope(getContext)
    val dispatcher = LocalSuspendingDispatcher.current
    return ScopedDispatcher(dispatcher, scope.coroutineContext)
}