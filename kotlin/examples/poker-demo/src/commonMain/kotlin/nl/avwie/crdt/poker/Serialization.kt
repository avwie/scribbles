package nl.avwie.crdt.poker

import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import nl.avwie.crdt.TombstoneResolver

val stateSerializersModule = SerializersModule {
    polymorphic(TombstoneResolver::class) {
        subclass(Participant.Tombstones::class)
    }
}