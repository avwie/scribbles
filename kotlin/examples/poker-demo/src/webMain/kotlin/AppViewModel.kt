import androidx.compose.runtime.mutableStateOf
import nl.avwie.crdt.poker.PokerState

class AppViewModel(
    initialState: PokerState,
    private val repository: Repository<PokerState>
) {

    var state = mutableStateOf(initialState)
        private set


}