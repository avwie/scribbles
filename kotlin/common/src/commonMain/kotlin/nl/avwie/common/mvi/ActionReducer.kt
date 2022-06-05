package nl.avwie.common.mvi

fun interface ActionReducer<S, A> {
    fun handleAction(state: S, action: A): S
}