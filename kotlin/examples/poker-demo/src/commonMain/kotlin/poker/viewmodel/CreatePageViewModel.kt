package poker.viewmodel

class CreatePageViewModel(
    private val onCreateRoom: (roomName: String) -> Unit = {}
) : PageViewModel() {
    fun createRoom(roomName: String) {
        onCreateRoom(roomName)
    }
}