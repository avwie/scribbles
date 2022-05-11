package nl.avwie.crdt

class PokerState constructor(
    private val nameCell: LWWCell<String>,
    val participants: TwoPhaseSet<Participant, String>
) : Mergeable<PokerState> {

    var name by nameCell

    constructor(name: String) : this(name.toLLWCell(), twoPhaseSetOf { it.name })

    override fun merge(other: PokerState): PokerState = PokerState(
        merge(nameCell, other.nameCell),
        merge(participants, other.participants)
    )
}

class Participant(
    private val nameCell: LWWCell<String>,
    private val pokerValueCell: LWWCell<Int?>
) : Mergeable<Participant> {

    var name by nameCell
    var pokerValue by pokerValueCell

    constructor(name: String) : this(name.toLLWCell(), null.toLLWCell())

    override fun merge(other: Participant): Participant = Participant(
        merge(nameCell, other.nameCell),
        merge(pokerValueCell, other.pokerValueCell)
    )
}