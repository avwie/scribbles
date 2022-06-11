package ui

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.forId
import org.jetbrains.compose.web.attributes.name
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLInputElement
import poker.viewmodel.RoomPageViewModel

@Composable
fun RoomPage(viewModel: RoomPageViewModel) {

    val pokerValues = listOf(1, 2, 3, 5, 8)

    Div {
        H1(attrs = { classes("text-center")}) { Text("Scrum Poker Room - ${viewModel.name}") }

        Div(attrs = { classes("card", "mb-2")}) {
            Div(attrs = { classes("card-header")}) {
                Text("Story")
            }

            Div(attrs = { classes("card-body")}) {
                Input(InputType.Text, attrs = {
                    classes("form-control")
                    placeholder("Enter story...")
                    defaultValue(viewModel.story)

                    onKeyUp { event ->
                        val target = (event.target as HTMLInputElement)
                        val story = target.value
                        viewModel.setStory(story)
                    }
                })
            }
        }

        Div(attrs = { classes("card") }) {
            Div(attrs = { classes("card-header") }) {
                Text("Participants")
            }
            Div(attrs = { classes("card-body") }) {
                Ul(attrs = { classes("list-group") }) {
                    viewModel.participants.forEach { participant ->
                        Li(attrs = {
                            classes("list-group-item")

                            if (!participant.isActive) {
                                classes("text-secondary")
                            }
                        }) {

                            Div(attrs = {
                                classes("d-flex", "justify-content-between")
                            }) {
                                Span {
                                    Text(participant.name)
                                }

                                if (participant.uuid == viewModel.participant?.uuid) {
                                    Span {
                                        pokerValues.forEach { value ->
                                            Div(attrs = { classes("form-check", "form-check-inline")}) {
                                                Input(InputType.Radio, attrs = {
                                                    name("score")
                                                    value(value)
                                                    classes("form-check-input")
                                                    checked(participant.score == value)
                                                    onClick { viewModel.setScore(value) }
                                                })
                                                Label(attrs = { classes("form-check-label") }) { Text(value.toString()) }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}