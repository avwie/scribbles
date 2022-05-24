package nl.avwie.crdt.poker

import common.UUIDFactory
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import nl.avwie.crdt.TombstoneResolver
import nl.avwie.crdt.immutable.toLWWValue
import nl.avwie.crdt.immutable.tombstoneSetOf
import nl.avwie.crdt.merge
import kotlin.random.Random
import kotlin.test.Test

class StateTests {

    @Test
    fun serializeAndDeserialize() {
        val stateA = PokerState("Foobar")
            .addParticipant("Henk")
            .addParticipant("Piet")
            .addParticipant("Kees")

        val stateB = PokerState("Foobaz")
            .addParticipant("Miep")

        val state = merge(stateA, stateB)

        val format = Json {
            serializersModule = stateSerializersModule
        }

        val serialized = format.encodeToString(state)
        val deserialized = format.decodeFromString<PokerState>(serialized)
    }
}