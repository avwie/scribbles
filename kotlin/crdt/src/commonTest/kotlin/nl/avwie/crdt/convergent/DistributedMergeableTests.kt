package nl.avwie.crdt.convergent

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
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
        val distributedMergeable = mergeableValueOf("Foo").asDistributed()
        val bus = distributedMergeable.bus

        val states = mutableListOf<String>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            bus.collect {
                states.add(it.value.value)
            }
        }

        distributedMergeable.update { mergeableValueOf("Bar") }

        runWhen({ states.size == 2}) {
            assertEquals(2, states.size)
            assertEquals("Foo", states[0])
            assertEquals("Bar", states[1])

            job.cancel()
        }
    }

    @Test
    fun distributedTest2() = runTest {
        val distributedMergeable = mergeableValueOf("Foo").asDistributed()
        val bus = distributedMergeable.bus

        val states = mutableListOf<DistributedMergeable.Update<MergeableValue<String>>>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            bus.collect {
                println("Receiving in collector: ${it}")
                states.add(it)
            }
        }

        val otherSource = uuid()

        distributedMergeable.update { mergeableValueOf("Bar") }
        bus.emit(DistributedMergeable.Update(otherSource, mergeableValueOf("Bar")))
        bus.emit(DistributedMergeable.Update(otherSource, mergeableValueOf("Baz")))

        runWhen({ states.size == 5}) {
            assertEquals(5, states.size)
        }
        job.cancel()
    }

    private suspend fun delayUntil(step: Long= 5, max: Long = 10000, predicate: () -> Boolean) {
        var t = 0L
        while (!predicate() && t < max) {
            delay(step)
            t += step
        }
        if (t > max) throw Error("Timout")
    }

    private suspend fun runWhen(predicate: () -> Boolean, block: () -> Unit) {
        delayUntil(predicate = predicate)
        block()
    }
}