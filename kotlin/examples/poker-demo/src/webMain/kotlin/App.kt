import nl.avwie.crdt.poker.PokerState
import org.jetbrains.compose.web.renderComposable
import ui.PokerApp

enum class Mode {
    Lobby,
    Poker
}

fun main() {
    val repository = LocalStorageRepository<PokerState>("POKER")
    val viewModel = AppViewModel(
        initialState = PokerState("Poker!"),
        repository = repository
    )

    renderComposable("root") {
        PokerApp(viewModel)
        /*val (mode, setMode) = remember { mutableStateOf(Mode.Lobby) }
        val state = storage.state

        when (mode) {
            Mode.Lobby -> Lobby(peoplePresent = state.participants.size, onParticipantCreated = { name ->
                setMode(Mode.Poker)
                storage.updateState(state.addParticipant(name))
            })
            Mode.Poker -> Poker(state, onUpdateState = { newState ->
                storage.updateState(newState) })
        }*/
    }
}

/*@Composable fun Lobby(peoplePresent: Int, onParticipantCreated: (participantName: String) -> Unit) {
    val (name, setName) = remember { mutableStateOf("") }

    H1 {
        Text("Enter your name")
    }
    H2 {
        Text("People present: $peoplePresent")
    }
    Input(InputType.Text) {
        onKeyUp { event -> setName((event.target as HTMLInputElement).value) }
    }

    Button(attrs = {
        if (name.isBlank()) disabled()

        onClick { onParticipantCreated(name) }
    }) {
        Text("Enter")
    }
}

@Composable fun Poker(state: PokerState, onUpdateState: (PokerState) -> Unit) {
    H1 {
        Text(state.name.value)
    }

    Ol {
        state.participants.elements.forEach { participant ->
            Li {
                Text(participant.name.value)
            }
        }
    }
}*/