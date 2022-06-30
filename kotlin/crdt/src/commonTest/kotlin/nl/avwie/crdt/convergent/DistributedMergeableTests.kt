package nl.avwie.crdt.convergent

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import nl.avwie.common.coroutines.DistributedMessage
import nl.avwie.common.uuid
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class DistributedMergeableTests {

    @Test
    fun singleUser() = runTest {
        launch {
            val (distributedMergeable, _) = mergeableValueOf("Foo").distribute(scope = this)
            distributedMergeable.update { mergeableValueOf("Bar") }
            assertEquals("Bar", distributedMergeable.value.value)
            cancel()
        }.join()
    }

    @Test
    fun incomingUpdates() = runTest {
        launch {
            val updates = MutableSharedFlow<DistributedMessage<MergeableValue<String>>>()
            val distributedMergeable = MergeableValue("Bar", Instant.fromEpochMilliseconds(0))
                .distributeIn(updates = updates, scope = this)

            runCurrent() // make sure the handlers are registered

            val otherSource = uuid()
            updates.emit(
                DistributedMessage(
                    otherSource,
                    MergeableValue("Baz", Instant.fromEpochMilliseconds(1))
            )
            )

            updates.emit(
                DistributedMessage(
                            otherSource,
                            MergeableValue("Bat", Instant.fromEpochMilliseconds(2))
                    )
            )

            runCurrent() // make sure the events have emitted
            assertEquals("Bat", distributedMergeable.value.value)
            cancel()
        }.join()
    }

    @Test
    fun distributedUpdates() = runTest {
        launch {
            val updates = MutableSharedFlow<DistributedMessage<MergeableValue<String>>>()
            val clientA = MergeableValue("Bar", Instant.fromEpochMilliseconds(0))
                .distributeIn(updates = updates, scope = this)
            val clientB = MergeableValue("Baz", Instant.fromEpochMilliseconds(0))
                .distributeIn(updates = updates, scope = this)
            runCurrent()

            assertEquals("Baz", clientA.value.value)
            assertEquals("Baz", clientB.value.value)

            clientA.update { mergeableValueOf("Bat") }
            runCurrent()

            assertEquals("Bat", clientA.value.value)
            assertEquals("Bat", clientB.value.value)
            cancel()
        }.join()
    }
}