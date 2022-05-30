package common.mvi

interface Dispatcher<A, E> : ActionDispatcher<A>, EffectDispatcher<E> {
}