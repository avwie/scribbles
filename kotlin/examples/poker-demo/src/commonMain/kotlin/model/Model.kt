package model

import common.UUID
import nl.avwie.crdt.convergent.*

data class Model(
    private val _name: MergeableValue<String>,
    val participants: MergeableMap<UUID, Participant>
): Mergeable<Model> {
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

    override fun merge(other: Model): Model = copy(
        _name = _name.merge(other._name),
        participants = participants.merge(other.participants)
    )
}

data class Participant(
    val uuid: UUID,
    private val _name: MergeableValue<String>,
    private val _score: MergeableValue<Int?>
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