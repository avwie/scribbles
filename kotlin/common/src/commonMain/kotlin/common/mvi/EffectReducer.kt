package common.mvi

import kotlinx.coroutines.flow.StateFlow

fun interface EffectReducer<S, A, E> {
    data class Result<A, E>(val action: A, val effects: Iterable<E>)
    suspend fun reduceEffect(state: StateFlow<S>, effect: E): Result<A, E>
}