package common.mvi

import kotlin.jvm.JvmName

interface Dispatcher<A, E> : ActionDispatcher<A>, EffectDispatcher<E>
interface SuspendingDispatcher<A, E> : ActionDispatcher<A>, SuspendingEffectDispatcher<E>

@JvmName("invokeAction")
operator fun <A> ActionDispatcher<A>.invoke(action: A) = this.dispatchAction(action)

@JvmName("invokeEffect")
operator fun <E> EffectDispatcher<E>.invoke(effect: E) = this.dispatchEffect(effect)

@JvmName("invokeEffect")
suspend operator fun <E> SuspendingEffectDispatcher<E>.invoke(effect: E) = this.dispatchEffect(effect)