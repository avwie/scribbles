package nl.avwie.common.mvi

interface ActionDispatcher<A> {
    fun dispatchAction(action: A)
}