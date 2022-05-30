package common.mvi

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.JvmName

class ScopedDispatcher<A, E>(
    private val dispatcher: Dispatcher<A, E>,
    override val coroutineContext: CoroutineContext
) : CoroutineScope, ActionDispatcher<A>, BlockingEffectDispatcher<E> {

    override fun dispatchAction(action: A) = dispatcher.dispatchAction(action)
    override fun dispatchEffect(effect: E) {
        launch {
            dispatcher.dispatchEffect(effect)
        }
    }

    @JvmName("invokeAction")
    operator fun invoke(action: A) = dispatchAction(action)

    @JvmName("invokeEffect")
    operator fun invoke(effect: E) = dispatchEffect(effect)

    operator fun component1(): (A) -> Unit = { dispatchAction(it) }
    operator fun component2(): (E) -> Unit = { dispatchEffect(it)}
}