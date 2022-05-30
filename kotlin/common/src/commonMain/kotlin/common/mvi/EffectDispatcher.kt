package common.mvi

import kotlinx.coroutines.Job

interface EffectDispatcher<E> {
    suspend fun dispatchEffect(effect: E)
}