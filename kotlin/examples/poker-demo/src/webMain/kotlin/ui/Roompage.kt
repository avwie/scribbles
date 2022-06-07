package ui

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.*
import poker.viewmodel.RoomPageViewModel

@Composable
fun RoomPage(viewModel: RoomPageViewModel) {

    Div {
        H1(attrs = { classes("text-center")}) { Text("Scrum Poker Room - ${viewModel.roomSharedState.name}") }

        Div(attrs = { classes("card") }) {
            Div(attrs = { classes("card-header") }) {
                Text("Participants")
            }
            Div(attrs = { classes("card-body") }) {
                Ul(attrs = { classes("list-group") }) {
                    viewModel.roomSharedState.participants.values.sortedBy { it.name }.forEach { participant ->
                        Li(attrs = {
                            classes("list-group-item")

                            if (!participant.isActive) {
                                classes("text-secondary")
                            }
                        }) {
                            Text(participant.name)
                        }
                    }
                }
            }
        }
    }
}