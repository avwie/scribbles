package nl.avwie.crdt.convergent

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
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
        val distributedMergeable = DistributedMergeable(mergeableValueOf("Foo"), scope = this)
        distributedMergeable.update { mergeableValueOf("Bar") }
        assertEquals("Bar", distributedMergeable.value.value)
    }

    @Test
    fun distributedTest2() = runTest {
        val updates = MutableSharedFlow<DistributedMergeable.Update<MergeableValue<String>>>()

        val distributedMergeable = DistributedMergeable(
                MergeableValue("Bar", Instant.fromEpochMilliseconds(0)),
                updates = updates,
                scope = this
        ).awaitInitialization()

        val otherSource = uuid()
        updates.emit(DistributedMergeable.Update(
                otherSource,
                MergeableValue("Baz", Instant.fromEpochMilliseconds(1))
        ))

        updates.emit(
                DistributedMergeable.Update(
                        otherSource,
                        MergeableValue("Bat", Instant.fromEpochMilliseconds(2))
                )
        )
        assertEquals("Bat", distributedMergeable.value.value)
    }
}