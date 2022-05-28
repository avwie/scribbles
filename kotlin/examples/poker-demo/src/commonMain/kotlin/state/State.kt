package state

import common.UUID
import kotlinx.serialization.SerialName
import nl.avwie.crdt.convergent.*

@kotlinx.serialization.Serializable
data class State(
    @SerialName("name") private val _name: MergeableValue<String>,
    val participants: MergeableMap<UUID, Participant>
): Mergeable<State> {
    val name by _name

    constructor(name: String): this(mergeableValueOf(name), mergeableMapOf())

    fun setName(name: String) = copy(_name = mergeableValueOf(name))

    fun putParticipant(participant: Participant) = copy(
        participants = participants.put(participant.uuid, participant)
    )

    fun updateParticipant(uuid: UUID, block: Participant.() -> Participant) = when {
        participants.contains(uuid) -> putParticipant(block(participants[uuid]!!))
        else -> throw IllegalArgumentException("Participant with UUID $uuid does not exist")
    }

    fun removeParticipant(uuid: UUID) = copy(
        participants = participants.remove(uuid)
    )

    override fun merge(other: State): State = copy(
        _name = _name.merge(other._name),
        participants = participants.merge(other.participants)
    )
}

@kotlinx.serialization.Serializable
data class Participant(
    val uuid: UUID,
    @SerialName("name") private val _name: MergeableValue<String>,
    @SerialName("score") private val _score: MergeableValue<Int?>
): Mergeable<Participant> {
    val name by _name
    val score by _score

    constructor(name: String): this(UUID.random(), mergeableValueOf(name), mergeableValueOf(null))

    fun setName(name: String) = copy(_name = mergeableValueOf(name))
    fun setScore(score: Int?) = copy(_score = mergeableValueOf(score))

    override fun merge(other: Participant): Participant = copy(
        _name = _name.merge(other._name),
        _score = _score.merge(other._score)

    )
}