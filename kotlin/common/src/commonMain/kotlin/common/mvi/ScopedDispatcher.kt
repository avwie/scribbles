package common.mvi

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.JvmName

class ScopedDispatcher<A, E>(
    private val suspendingDispatcher: SuspendingDispatcher<A, E>,
    override val coroutineContext: CoroutineContext
) : CoroutineScope, Dispatcher<A, E> {

    override fun dispatchAction(action: A) = suspendingDispatcher.dispatchAction(action)
    override fun dispatchEffect(effect: E) {
        launch {
            suspendingDispatcher.dispatchEffect(effect)
        }
    }

    @JvmName("invokeAction")
    operator fun invoke(action: A) = dispatchAction(action)

    @JvmName("invokeEffect")
    operator fun invoke(effect: E) = dispatchEffect(effect)

    operator fun component1(): (A) -> Unit = { dispatchAction(it) }
    operator fun component2(): (E) -> Unit = { dispatchEffect(it)}
}