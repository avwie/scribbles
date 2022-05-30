package common.mvi

interface Dispatcher<A, E> : ActionDispatcher<A>, EffectDispatcher<E>
interface SuspendingDispatcher<A, E> : ActionDispatcher<A>, SuspendingEffectDispatcher<E>