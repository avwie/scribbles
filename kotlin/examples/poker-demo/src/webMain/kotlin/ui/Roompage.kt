package ui

import androidx.compose.runtime.Composable
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.compose.web.attributes.*
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLInputElement
import poker.viewmodel.RoomPageViewModel
import kotlin.math.roundToInt

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
                Div(attrs = { classes("d-flex", "mb-2") }) {
                    Button(attrs = {
                        classes("btn", "btn-success", "me-2")

                        if (!viewModel.canReveal) disabled()
                        onClick { viewModel.reveal() }
                    }) {
                        Text("Reveal")
                    }

                    Button(attrs = {
                        classes("btn", "btn-warning", "me-2" )
                        onClick { viewModel.clearAllScores() }
                    }) {
                        Text("Clear")
                    }

                    Button(attrs = {
                        classes("btn", "btn-info" )
                        onClick {
                            window.alert("${document.location?.origin ?: ""}${viewModel.generateInvitation()}")
                        }
                    }) {
                        Text("Invite")
                    }
                }
                Ul(attrs = { classes("list-group") }) {
                    viewModel.participants.forEach { participant ->
                        Li(attrs = {
                            classes("list-group-item")

                            if (!participant.isActive) {
                                classes("text-secondary")
                            } else if (participant.score != null) {
                                classes("text-success")
                            }
                        }) {

                            Div(attrs = {
                                classes("d-flex", "justify-content-between", "align-items-center")
                                style {
                                    height(2.5.em)
                                }
                            }) {
                                Span {
                                    Text(participant.name)
                                }

                                if (viewModel.revealed) {
                                    H4 {
                                        Span(attrs = { classes("badge", "bg-secondary")} ) {
                                            participant.score?.also {
                                                Text(it.toString())
                                            }
                                        }
                                    }
                                } else {
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

                    if (viewModel.revealed) {
                        Li(attrs = { classes("list-group-item") }) {
                            Div(attrs = {
                                classes("d-flex", "justify-content-between", "align-items-center")
                                style {
                                    height(2.5.em)
                                }
                            }) {
                                B {
                                    Text("Average")
                                }
                                H4 {
                                    Span(attrs = { classes("badge", "bg-success")} ) {
                                        ((viewModel.average * 100).roundToInt() / 100.0).also {
                                            Text(it.toString())
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