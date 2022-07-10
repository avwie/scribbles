package nl.avwie.examples.crdt

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import nl.avwie.crdt.convergent.Mergeable
import nl.avwie.crdt.convergent.MergeableStateFlow

interface MutableMergeableStateFlow<T : Mergeable<T>> {
    val value: T

    operator fun component1(): T
    operator fun component2(): ((T) -> T) -> Unit
}

fun <T : Mergeable<T>> MergeableStateFlow<T>.collectAsMutableState(
    scope: CoroutineScope
): MutableMergeableStateFlow<T> = object : MutableMergeableStateFlow<T> {

    private val internalState = mutableStateOf(this@collectAsMutableState.value)

    init {
        this@collectAsMutableState
            .onEach { value -> internalState.value = value }
            .launchIn(scope)
    }

    override val value: T
        get() = internalState.value

    override fun component1(): T {
        return internalState.value
    }

    override fun component2(): ((T) -> T) -> Unit {
        return { updater -> this@collectAsMutableState.update(updater) }
    }
}