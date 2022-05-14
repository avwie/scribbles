package nl.avwie.crdt

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
class PokerState constructor(
    private val _name: LWWCell<String>,
    val participants: ObservedRemovedSet<Participant, String>
) : Mergeable<PokerState> {

    var name by _name

    constructor(name: String) : this(name.toLWWCell(), observedRemovedSetOf(tombstoneStrategy = Participant.Tombstones))

    override fun merge(other: PokerState): PokerState = PokerState(
        _name = merge(_name, other._name),
        participants = merge(participants, other.participants)
    )
}

@kotlinx.serialization.Serializable
class Participant(
    private val _name: LWWCell<String>,
    private val _pokerValue: LWWCell<Int>
) : Mergeable<Participant> {

    var name by _name
    var pokerValue by _pokerValue

    constructor(name: String) : this(name.toLWWCell(), (-1).toLWWCell())

    override fun merge(other: Participant): Participant = Participant(
        _name = merge(_name, other._name),
        _pokerValue = merge(_pokerValue, other._pokerValue)
    )

    @kotlinx.serialization.Serializable
    @SerialName("participant")
    object Tombstones : TombstoneResolver<Participant, String> {
        override fun tombstoneOf(item: Participant): String = item.name
    }
}