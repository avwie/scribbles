package nl.avwie.crdt.convergent

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class MergeableStateFlowTests {

    @Test
    fun collect() = runTest {
        val flow = mergeableValueOf(0).asStateFlow()
        val collected = mutableListOf<Int>()

        flow.onEach { state -> collected.add(state.value) }.launchIn(scope = this)
        runCurrent()

        flow.update { mergeableValueOf(1) }
        runCurrent()

        flow.update { mergeableValueOf(2) }
        runCurrent()

        assertEquals(3, collected.size)
        assertEquals(listOf(0, 1, 2), collected)
        currentCoroutineContext().cancelChildren()
    }

    @Test
    fun merge() = runTest {
        val flow = MergeableValue(0, Instant.fromEpochMilliseconds(0)).asStateFlow()
        val collected = mutableListOf<Int>()

        flow.onEach { state -> collected.add(state.value) }.launchIn(scope = this)
        runCurrent()

        flow.merge(MergeableValue(1, Instant.fromEpochMilliseconds(1)))
        runCurrent()

        assertEquals(1, flow.value.value)

        flow.merge(MergeableValue(2, Instant.fromEpochMilliseconds(0)))
        runCurrent()

        assertEquals(1, flow.value.value)
        currentCoroutineContext().cancelChildren()
    }

    @Test
    fun multiple() = runTest {
        val flowA = MergeableValue(0, Instant.fromEpochMilliseconds(0)).asStateFlow()
        val flowB = MergeableValue(1, Instant.fromEpochMilliseconds(1)).asStateFlow()
        runCurrent()

        flowA.mergeWith(flowB, scope = this)
        flowB.mergeWith(flowA, scope = this)
        runCurrent()

        flowB.onEach { update -> flowA.merge(update) }.launchIn(scope = this)
        runCurrent()

        assertEquals(flowA.value, flowB.value)
        assertEquals(1, flowA.value.value)

        flowA.update { MergeableValue(2, Instant.fromEpochMilliseconds(2)) }
        runCurrent()

        assertEquals(flowA.value, flowB.value)
        assertEquals(2, flowA.value.value)
        currentCoroutineContext().cancelChildren()
    }
}