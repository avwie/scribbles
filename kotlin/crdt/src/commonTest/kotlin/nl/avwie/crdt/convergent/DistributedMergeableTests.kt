package nl.avwie.crdt.convergent

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.cancel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import nl.avwie.common.uuid
import kotlin.test.Test
import kotlin.test.assertEquals

class DistributedMergeableTests {

    @Test
    fun distributedTest() = runTest {
        selfCancellingScope {
            val (state, _) = mergeableValueOf("Foo").distribute(this)
            state.update { mergeableValueOf("Bar") }
            assertEquals("Bar", state.value.value)
        }
    }

    @Test
    fun distributedTest2() = runTest {
        selfCancellingScope {
            val (state, updates) = MergeableValue("Bar", Instant.fromEpochMilliseconds(0)).distribute(this)

            val otherSource = uuid()
            launch {
                updates.emit(
                    DistributedMergeable.Update(
                        otherSource,
                        MergeableValue("Baz", Instant.fromEpochMilliseconds(1))
                    )
                )
                updates.emit(
                    DistributedMergeable.Update(
                        otherSource,
                        MergeableValue("Bat", Instant.fromEpochMilliseconds(2))
                    )
                )
            }
            advanceUntilIdle()
            assertEquals("Bat", state.value.value)
        }
    }

    suspend inline fun selfCancellingScope(crossinline block: CoroutineScope.() -> Unit) {
        try {
            coroutineScope {
                block(this)
                cancel()
            }
        } catch (ex: CancellationException) {
            println("Needed to cancel the the inner scope")
        }
    }
}