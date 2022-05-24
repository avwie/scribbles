package model

import common.sleep
import nl.avwie.crdt.convergent.merge
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ModelTests {

    @Test
    fun merge() {
        val modelA = Model("Foo")
            .putParticipant(Participant("Henk"))
            .putParticipant(Participant("Piet"))
            .putParticipant(Participant("Kees"))

        sleep(1)

        val modelB = Model("Bar")
            .putParticipant(Participant("Miep"))
            .putParticipant(Participant("Toos"))

        val merged = merge(modelA, modelB)
        assertEquals("Bar", merged.name)
        assertEquals(5, merged.participants.size)
    }

    @Test
    fun updateParticipantAndMerge() {
        val modelA = Model("Foo")
            .putParticipant(Participant("Henk"))
            .putParticipant(Participant("Piet"))
            .putParticipant(Participant("Kees"))

        sleep(1)

        val modelB = Model("Bar")
            .putParticipant(Participant("Miep"))
            .putParticipant(Participant("Toos"))

        sleep(1)
        val miep = modelB.participants.values.find { it.name == "Miep" }!!

        val merged = merge(
            modelA.putParticipant(miep.setName("Miep 2").setScore(2)),
            modelB
        )
        assertEquals(5, merged.participants.size)
        assertEquals(1, merged.participants.count { it.value.name == "Miep 2" })
    }
}