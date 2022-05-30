package common.mvi

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

interface EffectDispatcher<E> {
    val scope: CoroutineScope
    fun dispatchEffect(effect: E): Job
}