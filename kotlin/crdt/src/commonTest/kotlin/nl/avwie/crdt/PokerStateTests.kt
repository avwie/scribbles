package nl.avwie.crdt

import common.sleep
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
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

    @Test
    fun serialize() {
        val state = PokerState("Foobar")
        val arjan = Participant("Arjan")
        val henk = Participant("Henk")
        val piet = Participant("Piet")
        state.participants.addAll(listOf(arjan, henk, piet))
        state.participants.remove(piet)
        state.participants.add(piet)

        val module = SerializersModule {
            polymorphic(TombstoneResolver::class) {
                subclass(Participant.Tombstones::class)
            }
        }

        val format = Json {
            serializersModule = module
            prettyPrint = true
        }
        val serialized = format.encodeToString(state)
        val deserialized = format.decodeFromString<PokerState>(serialized)
    }
}