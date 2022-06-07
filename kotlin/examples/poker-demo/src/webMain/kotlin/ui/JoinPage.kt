package ui

import androidx.compose.runtime.*
import org.jetbrains.compose.web.attributes.*
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLInputElement
import poker.viewmodel.JoinPageViewModel

@Composable
fun JoinPage(viewModel: JoinPageViewModel) {
    var participantName by remember { mutableStateOf("") }

    Div {
        H2(attrs = { classes("text-center") }) {
            Text("Joining ${viewModel.roomSharedState.name}")
        }

        H5(attrs = { classes("text-center") }) {
            Text("Active participants: ${viewModel.participantCount}")
        }

        Input(InputType.Text, attrs = {
            classes("form-control", "mt-3")
            placeholder("Enter your name")
            onKeyUp { event -> participantName = (event.target as HTMLInputElement).value.trim() }
        })

        Button(attrs = {
            classes("btn", "btn-primary", "mt-3", "w-100")
            type(ButtonType.Button)

            if (participantName.length < 2) disabled()

            onClick {
                viewModel.joinRoom(participantName)
            }
        }) {
            Text("Join")
        }
    }
}