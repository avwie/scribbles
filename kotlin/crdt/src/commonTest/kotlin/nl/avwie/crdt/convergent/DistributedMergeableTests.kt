package nl.avwie.crdt.convergent

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import nl.avwie.common.uuid
import kotlin.test.Test
import kotlin.test.assertEquals

class DistributedMergeableTests {

    @Test
    fun distributedTest() = runTest {
        val (state, _, job) = mergeableValueOf("Foo").distribute(this)
        state.update { mergeableValueOf("Bar") }
        assertEquals("Bar", state.value.value)
        job.cancel()
    }

    @Test
    fun distributedTest2() = runTest {
        val (state, updates, job) = MergeableValue("Bar", Instant.fromEpochMilliseconds(0)).distribute(this)
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
        runCurrent()
        assertEquals("Bat", state.value.value)
        job.cancel()
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