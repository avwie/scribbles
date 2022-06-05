package ui

import androidx.compose.runtime.*
import org.jetbrains.compose.web.attributes.*
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLInputElement
import poker.viewmodel.CreateViewModel

@Composable
fun CreatePage(viewModel: CreateViewModel) {
    var roomName by remember { mutableStateOf("") }

    Div {
        H2(attrs = { classes("text-center")}) { Text("Create or join room") }

        Input(InputType.Text, attrs = {
            classes("form-control", "mt-3")
            placeholder("Enter room name")
            onKeyUp { event -> roomName = (event.target as HTMLInputElement).value.trim() }
        })

        Button(attrs = {
            classes("btn", "btn-primary", "mt-3", "w-100")
            type(ButtonType.Button)

            if (roomName.length < 4) disabled()

            onClick {
                viewModel.createRoom(roomName)
            }
        }) {
            Text("Enter")
        }
    }
}