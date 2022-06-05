package nl.avwie.common.mvi

interface EffectDispatcher<E> {
    fun dispatchEffect(effect: E)
}

interface SuspendingEffectDispatcher<E> {
    suspend fun dispatchEffect(effect: E)
}