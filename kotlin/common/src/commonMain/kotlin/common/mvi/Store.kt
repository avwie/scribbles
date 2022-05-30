package common.mvi

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.coroutines.CoroutineContext

class Store<S, A, E>(
    initialState: S,
    private val actionReducer: ActionReducer<S, A>,
    private val effectHandler: EffectHandler<S, A, E>,
    override val coroutineContext: CoroutineContext = Dispatchers.Unconfined
) : Dispatcher<A, E> {

    private val _state = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    override fun dispatchAction(action: A) {
        _state.update { state -> actionReducer.handleAction(state, action) }
    }

    override fun dispatchEffect(effect: E) {
        launch {
            effectHandler.reduceEffect(state, effect, this@Store)
        }
    }
}