package common.mvi

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class Store<S, A, E>(
    initialState: S,
    private val actionReducer: ActionReducer<S, A>,
    private val effectReducer: EffectReducer<S, A, E>,
    override val scope: CoroutineScope = CoroutineScope(Dispatchers.Unconfined)
) : ActionDispatcher<A>, EffectDispatcher<E> {

    private val _state = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    override fun dispatchAction(action: A) {
        _state.update { state -> actionReducer.handleAction(state, action) }
    }

    override fun dispatchEffect(effect: E): Job {
        return scope.launch {
            val result = effectReducer.reduceEffect(state, effect)
            dispatchAction(result.action)
            result.effects.forEach { effect -> dispatchEffect(effect) }
        }
    }
}