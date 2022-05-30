package common.mvi

interface BlockingEffectDispatcher<E> {
    fun dispatchEffect(effect: E)
}

interface EffectDispatcher<E> {
    suspend fun dispatchEffect(effect: E)
}