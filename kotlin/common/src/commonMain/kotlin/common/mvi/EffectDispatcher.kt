package common.mvi

import kotlinx.coroutines.CoroutineScope

interface EffectDispatcher<E> : CoroutineScope {
    fun dispatchEffect(effect: E)
}