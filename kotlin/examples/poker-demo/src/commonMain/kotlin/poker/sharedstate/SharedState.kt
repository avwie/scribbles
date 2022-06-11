package poker.sharedstate

import kotlinx.serialization.SerialName
import nl.avwie.common.UUID
import nl.avwie.crdt.convergent.*

@kotlinx.serialization.Serializable
data class RoomState(
    @SerialName("name") private val _name: MergeableValue<String>,
    @SerialName("story") private val _story: MergeableValue<String>,
    @SerialName("revealed") private val _revealed: MergeableValue<Boolean>,
    val participants: MergeableMap<UUID, Participant>
): Mergeable<RoomState> {
    val name by _name
    val story by _story
    val revealed by _revealed

    constructor(name: String): this(
        mergeableValueOf(name),
        mergeableDistantPastValueOf(""),
        mergeableDistantPastValueOf(false),
        mergeableMapOf()
    )

    fun setName(name: String) = copy(_name = mergeableValueOf(name))
    fun setStory(story: String) = copy(_story = mergeableValueOf(story))
    fun setRevealed(revealed: Boolean) = copy(_revealed = mergeableValueOf(revealed))

    fun putParticipant(participant: Participant) = copy(
        participants = participants.put(participant.uuid, participant)
    )

    fun updateParticipant(uuid: UUID, block: Participant.() -> Participant) = when {
        participants.contains(uuid) -> putParticipant(block(participants[uuid]!!))
        else -> throw IllegalArgumentException("poker.model.Participant with UUID $uuid does not exist")
    }

    fun removeParticipant(uuid: UUID) = updateParticipant(uuid) { setInactive() }

    override fun merge(other: RoomState): RoomState = copy(
        _name = _name.merge(other._name),
        _story = _story.merge(other._story),
        _revealed = _revealed.merge(other._revealed),
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
        _score = _score.merge(other._score),
        _active = _active.merge(other._active)

    )
}