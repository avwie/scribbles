package nl.avwie.crdt

import common.sleep
import kotlin.test.Test
import kotlin.test.assertEquals

class PokerStateTests {

    @Test
    fun simple() {
        repeat(100) {
            val pokerState1 = PokerState("Foobar")
            pokerState1.participants.add(Participant("Arjan"))
            pokerState1.participants.add(Participant("Henk"))
            sleep(1)

            val pokerState2 = PokerState("Foobar updated")
            pokerState2.participants.add(Participant("Piet"))
            sleep(1)

            val pokerState3 = PokerState("Foobar updated")
            pokerState3.participants.add(Participant("Geert"))

            val merged = merge(pokerState1, pokerState2, pokerState3)
            assertEquals("Foobar updated", merged.name)
            assertEquals(4, merged.participants.size)
        }
    }
}