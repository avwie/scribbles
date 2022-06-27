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

@OptIn(ExperimentalCoroutinesApi::class)
class DistributedMergeableTests {

    @Test
    fun singleUser() = runTest {
        launch {
            val distributedMergeable = DistributedMergeable(mergeableValueOf("Foo"), scope = this)
            distributedMergeable.update { mergeableValueOf("Bar") }
            assertEquals("Bar", distributedMergeable.value.value)
            distributedMergeable.close()
        }.join()
    }

    @Test
    fun incomingUpdates() = runTest {
        launch {
            val updates = MutableSharedFlow<DistributedMergeable.Update<MergeableValue<String>>>()
            val distributedMergeable = DistributedMergeable(
                    MergeableValue("Bar", Instant.fromEpochMilliseconds(0)),
                    updates = updates,
                    scope = this
            )
            runCurrent() // make sure the handlers are registered

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

            runCurrent() // make sure the events have emitted
            assertEquals("Bat", distributedMergeable.value.value)
            distributedMergeable.close()
        }.join()
    }

    @Test
    fun distributedUpdates() = runTest {
        launch {
            val updates = MutableSharedFlow<DistributedMergeable.Update<MergeableValue<String>>>()

            val clientA = DistributedMergeable(
                    MergeableValue("Bar", Instant.fromEpochMilliseconds(0)),
                    updates = updates,
                    scope = this
            )
            runCurrent()

            val clientB = DistributedMergeable(
                    MergeableValue("Baz", Instant.fromEpochMilliseconds(1)),
                    updates = updates,
                    scope = this
            )
            runCurrent()

            assertEquals("Baz", clientA.value.value)
            assertEquals("Baz", clientB.value.value)

            clientA.update { mergeableValueOf("Bat") }
            runCurrent()

            assertEquals("Bat", clientA.value.value)
            assertEquals("Bat", clientB.value.value)

            clientA.close()
            clientB.close()
        }.join()
    }
}