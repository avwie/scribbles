import androidx.compose.runtime.LaunchedEffect
import common.messagebus.BrowserLocalStorageMessageBus
import common.messagebus.SerializingMessageBus
import kotlinx.browser.window
import org.jetbrains.compose.web.renderComposable
import poker.model.RoomModel
import poker.viewmodel.ViewModel
import poker.viewmodel.ViewState
import ui.CreateOrJoinRoomPage
import ui.ParticipantInfoPage

val messageBus = BrowserLocalStorageMessageBus("poker")
val viewModel = ViewModel(messageBus)

fun main() {
    renderComposable("root") {

        LaunchedEffect(Unit) {
            window.onbeforeunload = {
                viewModel.leave()
                null
            }
        }

        when (viewModel.viewState) {
            ViewState.CreateOrJoin -> CreateOrJoinRoomPage(onEnterRoom = viewModel::enterRoomName)
            ViewState.ParticipantInfo -> ParticipantInfoPage(
                roomName = viewModel.roomState.name,
                activeParticipants = viewModel.participantCount,
                onEnterName = viewModel::enterParticipantName
            )
            ViewState.Room -> {}
        }
    }
}