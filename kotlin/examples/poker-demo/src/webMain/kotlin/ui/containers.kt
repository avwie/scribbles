package ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import org.jetbrains.compose.web.attributes.*
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLInputElement

@Composable fun FullPageCenter(contentBuilder: ContentBuilder<HTMLDivElement>) {
    Div(attrs = {
        classes("d-flex", "justify-content-center", "align-items-center", "vw-100", "vh-100")
    }) {
        contentBuilder()
    }
}

@Composable fun ErrorPage(href: String) {
    FullPageCenter {
        Div(attrs = { classes("text-center")}) {
            H2(attrs = { classes("text-danger") }) {
                Text("This page does not exist!")
            }
            P {
                Text("The URL you were trying to visit was")
            }

            I {
                Text(href)
            }
        }
    }
}

@Composable fun LandingPage(
    onEnterRoom: (roomName: String) -> Unit = {}
) {
    var roomName by remember { mutableStateOf("") }

    FullPageCenter {
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
                    onEnterRoom(roomName)
                }
            }) {
                Text("Enter")
            }
        }
    }
}

@Composable fun JoinPage(
    roomName: String,
    activeParticipants: Int,
    onEnterName: (participantName: String) -> Unit = {}
) {
    var participantName by remember { mutableStateOf("") }

    FullPageCenter {
        Div {
            H2(attrs = { classes("text-center") }) {
                Text("Joining $roomName")
            }

            H5(attrs = { classes("text-center") }) {
                Text("Active participants: $activeParticipants")
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
                    onEnterName(participantName)
                }
            }) {
                Text("Join")
            }
        }
    }
}

@Composable fun Roompage() {

}