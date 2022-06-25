package nl.avwie.crdt.convergent

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import nl.avwie.common.uuid
import kotlin.test.Test
import kotlin.test.assertEquals

class DistributedMergeableTests {

    @Test
    fun distributedTest() = runTest {
        val (state, bus) = mergeableValueOf("Foo").asDistributed()
        val states = mutableListOf<String>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            bus.take(2).collect {
                println("Receiving in collector: ${it}")
                states.add(it.state.value)
            }
        }

        state.update { mergeableValueOf("Bar") }

        job.join()
        assertEquals(2, states.size)
        assertEquals("Foo", states[0])
        assertEquals("Bar", states[1])
    }

    @Test
    fun distributedTest2() = runTest {
        val (state, bus) = mergeableValueOf("Foo").asDistributed()

        val states = mutableListOf<DistributedMergeable.Update<MergeableValue<String>>>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            bus.collect {
                println("Receiving in collector: ${it}")
                states.add(it)
            }
        }

        val otherSource = uuid()

        state.update { mergeableValueOf("Bar") }
        bus.emit(DistributedMergeable.Update(otherSource, mergeableValueOf("Bar")))
        bus.emit(DistributedMergeable.Update(otherSource, mergeableValueOf("Baz")))

        delayUntil { states.size == 5 }
        job.cancel()

        assertEquals(5, states.size)
    }

    private suspend fun delayUntil(step: Long= 5, max: Long = 10000, predicate: () -> Boolean) {
        var t = 0L
        while (!predicate() && t < max) {
            delay(step)
            t += step
        }
    }
}