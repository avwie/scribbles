package poker.sharedstate

import kotlinx.serialization.SerialName
import nl.avwie.common.UUID
import nl.avwie.crdt.convergent.*

@kotlinx.serialization.Serializable
data class RoomSharedState(
    @SerialName("name") private val _name: MergeableValue<String>,
    @SerialName("story") private val _story: MergeableValue<String>,
    val participants: MergeableMap<UUID, Participant>
): Mergeable<RoomSharedState> {
    val name by _name

    constructor(name: String): this(mergeableValueOf(name), mergeableValueOf(""), mergeableMapOf())

    fun setName(name: String) = copy(_name = mergeableValueOf(name))

    fun setStory(story: String) = copy(_story = mergeableValueOf(story))

    fun putParticipant(participant: Participant) = copy(
        participants = participants.put(participant.uuid, participant)
    )

    fun updateParticipant(uuid: UUID, block: Participant.() -> Participant) = when {
        participants.contains(uuid) -> putParticipant(block(participants[uuid]!!))
        else -> throw IllegalArgumentException("poker.model.Participant with UUID $uuid does not exist")
    }

    fun removeParticipant(uuid: UUID) = updateParticipant(uuid) { setInactive() }

    override fun merge(other: RoomSharedState): RoomSharedState = copy(
        _name = _name.merge(other._name),
        participants = participants.merge(other.participants)
    )
}

@kotlinx.serialization.Serializable
data class Participant(
    val uuid: UUID,
    @SerialName("name") private val _name: MergeableValue<String>,
    @SerialName("score") private val _score: MergeableValue<Int?>,
    @SerialName("active") private val _active: MergeableValue<Boolean>
): Mergeable<Participant> {
    val name by _name
    val score by _score
    val isActive by _active

    constructor(name: String): this(
        UUID.random(),
        mergeableValueOf(name),
        mergeableValueOf(null),
        mergeableValueOf(true)
    )

    fun setName(name: String) = copy(_name = mergeableValueOf(name))
    fun setScore(score: Int?) = copy(_score = mergeableValueOf(score))
    fun setActive() = if (isActive) this else copy(_active = mergeableValueOf(true))
    fun setInactive() = if (isActive) copy(_active = mergeableValueOf(false)) else this

    override fun merge(other: Participant): Participant = copy(
        _name = _name.merge(other._name),
        _score = _score.merge(other._score)

    )
}