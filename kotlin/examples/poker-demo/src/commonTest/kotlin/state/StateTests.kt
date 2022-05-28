package state

import common.sleep
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import nl.avwie.crdt.convergent.merge
import kotlin.test.Test
import kotlin.test.assertEquals

class StateTests {

    @Test
    fun merge() {
        val stateA = State("Foo")
            .putParticipant(Participant("Henk"))
            .putParticipant(Participant("Piet"))
            .putParticipant(Participant("Kees"))

        sleep(1)

        val stateB = State("Bar")
            .putParticipant(Participant("Miep"))
            .putParticipant(Participant("Toos"))

        val merged = merge(stateA, stateB)
        assertEquals("Bar", merged.name)
        assertEquals(5, merged.participants.size)
    }

    @Test
    fun updateParticipantAndMerge() {
        val stateA = State("Foo")
            .putParticipant(Participant("Henk"))
            .putParticipant(Participant("Piet"))
            .putParticipant(Participant("Kees"))

        sleep(1)

        val stateB = State("Bar")
            .putParticipant(Participant("Miep"))
            .putParticipant(Participant("Toos"))

        sleep(1)
        val miep = stateB.participants.values.find { it.name == "Miep" }!!

        val merged = merge(
            stateA.putParticipant(miep.setName("Miep 2").setScore(2)),
            stateB
        )
        assertEquals(5, merged.participants.size)
        assertEquals(1, merged.participants.count { it.value.name == "Miep 2" })
    }

    @Test
    fun serialize() {
        val stateA = State("Foo")
            .putParticipant(Participant("Henk"))
            .putParticipant(Participant("Piet"))
            .putParticipant(Participant("Kees"))

        sleep(1)

        val stateB = State("Bar")
            .putParticipant(Participant("Miep"))
            .putParticipant(Participant("Toos"))

        val merged = merge(stateA, stateB)
        val serialized = Json.encodeToString(merged)
        val deserialized = Json.decodeFromString<State>(serialized)
        assertEquals(merged, deserialized)
    }
}