package common.mvi

import kotlinx.coroutines.flow.StateFlow

fun interface EffectHandler<S, A, E> {
    suspend fun handleEffect(
        state: StateFlow<S>,
        effect: E,
        dispatcher: Dispatcher<A, E>
    )
}