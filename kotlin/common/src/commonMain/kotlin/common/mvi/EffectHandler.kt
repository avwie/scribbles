package common.mvi

import kotlinx.coroutines.flow.StateFlow

fun interface EffectHandler<S, A, E> {
    suspend fun reduceEffect(
        state: StateFlow<S>,
        effect: E,
        actionDispatcher: ActionDispatcher<A>,
        effectDispatcher: EffectDispatcher<E>
    )
}