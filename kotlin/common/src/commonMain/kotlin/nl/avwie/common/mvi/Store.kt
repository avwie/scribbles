package nl.avwie.common.mvi

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface Store<S, A, E> : SuspendingDispatcher<A, E> {
    val state: StateFlow<S>

    companion object {
        operator fun <S, A, E> invoke(
            initialState: S,
            actionReducer: ActionReducer<S, A>,
            effectHandler: EffectHandler<S, A, E>
        ): Store<S, A, E> = StoreImpl(initialState, actionReducer, effectHandler)
    }
}

class StoreImpl<S, A, E>(
    initialState: S,
    private val actionReducer: ActionReducer<S, A>,
    private val effectHandler: EffectHandler<S, A, E>,
) : Store<S, A, E> {

    private val _state = MutableStateFlow(initialState)
    override val state = _state.asStateFlow()

    override fun dispatchAction(action: A) {
        _state.update { state -> actionReducer.handleAction(state, action) }
    }

    override suspend fun dispatchEffect(effect: E) {
        effectHandler.handleEffect(state, effect, this@StoreImpl)
    }
}