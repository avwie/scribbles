package poker.util

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

fun <T> StateFlow<T>.collectAsState(scope: CoroutineScope): State<T> {
    val state = mutableStateOf(this.value)
    this.onEach { update ->
        state.value = update
    }.launchIn(scope)
    return state
}