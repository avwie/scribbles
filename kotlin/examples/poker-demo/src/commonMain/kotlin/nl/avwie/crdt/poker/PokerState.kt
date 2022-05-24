package nl.avwie.crdt.poker

import common.UUID
import kotlinx.serialization.SerialName
import nl.avwie.crdt.Mergeable
import nl.avwie.crdt.TombstoneResolver
import nl.avwie.crdt.immutable.LWWValue
import nl.avwie.crdt.immutable.TombstoneSet
import nl.avwie.crdt.immutable.toLWWValue
import nl.avwie.crdt.immutable.tombstoneSetOf

@kotlinx.serialization.Serializable
data class PokerState(
    val name: LWWValue<String>,
    val revealed: LWWValue<Boolean>,
    val participants: TombstoneSet<Participant, UUID>
) : Mergeable<PokerState> {
    constructor(name: String): this(name.toLWWValue(), false.toLWWValue(), tombstoneSetOf(tombstoneResolver = Participant.Tombstones))

    fun addParticipant(name: String) = copy(participants = participants.add(Participant(name)))

    fun removeParticipant(participant: Participant) = copy(participants = participants.remove(participant))

    fun updateParticipantName(participant: Participant, name: String) = copy(
        participants = participants.update(participant) { it.updateName(name) }
    )

    fun updateParticipantValue(participant: Participant, value: Int?) = copy(
        participants = participants.update(participant) { it.updateValue(value) }
    )

    fun updateReveal(revealed: Boolean) = copy(revealed = this.revealed.update(revealed))

    override fun merge(other: PokerState): PokerState = copy(
        name = name.merge(other.name),
        revealed = revealed.merge(other.revealed),
        participants = participants.merge(other.participants)
    )
}

@kotlinx.serialization.Serializable
data class Participant(
    val id: UUID,
    val name: LWWValue<String>,
    val value: LWWValue<Int?>
) : Mergeable<Participant> {
    constructor(name: String): this(UUID.random(), name.toLWWValue(), null.toLWWValue())

    fun updateName(name: String) = copy(name = this.name.update(name))
    fun updateValue(value: Int?) = copy(value = this.value.update(value))

    override fun merge(other: Participant): Participant = copy(
        name = name.merge(other.name),
        value = value.merge(other.value)
    )

    @kotlinx.serialization.Serializable
    @SerialName("participant")
    object Tombstones : TombstoneResolver<Participant, UUID> {
        override fun tombstoneOf(item: Participant): UUID = item.id
    }
}

