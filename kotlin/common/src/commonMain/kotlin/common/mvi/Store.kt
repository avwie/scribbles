package common.mvi

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class Store<S, A, E>(
    initialState: S,
    private val actionReducer: ActionReducer<S, A>,
    private val effectHandler: EffectHandler<S, A, E>,
) : Dispatcher<A, E> {

    private val _state = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    override fun dispatchAction(action: A) {
        _state.update { state -> actionReducer.handleAction(state, action) }
    }

    override suspend fun dispatchEffect(effect: E) {
        effectHandler.handleEffect(state, effect, this@Store)
    }
}